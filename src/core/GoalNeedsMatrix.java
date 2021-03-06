/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import GUI.Helper.NodeFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Predicate;

import GUI.MainController;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ltrask
 */
public class GoalNeedsMatrix implements Serializable {

    private static final long serialVersionUID = 123456789L;

    public ObservableList<QuestionYN> qList;

    public ObservableList<Need> needsList;

    private LinkedHashMap<Question, Integer> qToRowMap = new LinkedHashMap();

    private LinkedHashMap<Need, Integer> needToColMap = new LinkedHashMap();

    private final HashMap<String, SimpleIntegerProperty> hasGoalCat;

    //private int numMob;
    //private int numProd;
    //private int numReg;
    //private int numSafety;
    //private int numTravInfo;
    public int[][] matrix;

    public GoalNeedsMatrix(ObservableList<QuestionYN> qList, ObservableList<Need> needsList, ObservableList<QuestionYN> majorGoalsList) {
        this.qList = qList;
        for (int qIdx = 0; qIdx < qList.size(); qIdx++) {
            qToRowMap.put(qList.get(qIdx), qIdx); //qList.get(qIdx).getIdx() - 1
            //System.out.println("Question: " + qList.get(qIdx).getQuestionText() + ", Row: " + String.valueOf(qIdx));
        }
        this.needsList = needsList;
        for (int needIdx = 0; needIdx < needsList.size(); needIdx++) {
            needToColMap.put(needsList.get(needIdx), needIdx);
            //System.out.println("Need: " + needsList.get(needIdx).getDescription() + ", Col: " + String.valueOf(needIdx));
        }

        hasGoalCat = new HashMap();
        hasGoalCat.put(Question.GOAL_MOBILITY, majorGoalsList.get(0).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_SAFETY, majorGoalsList.get(1).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_PROD, majorGoalsList.get(2).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_REG, majorGoalsList.get(3).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_TRAVELER_INFO, majorGoalsList.get(4).responseIdxProperty());

        connectNeedsProperties();

        matrix = new int[qList.size()][needsList.size()];

        loadDefaultV2();

    }

    public GoalNeedsMatrix(GoalNeedsMatrix gnMat, ObservableList<QuestionYN> majorGoalsList) {
        qList = gnMat.qList;
        qToRowMap = new LinkedHashMap();
        for (int qIdx = 0; qIdx < qList.size(); qIdx++) {
            qToRowMap.put(qList.get(qIdx), qIdx);  //qList.get(qIdx).getIdx() - 1
        }
        needsList = gnMat.needsList;
        needToColMap = new LinkedHashMap();
        for (int needIdx = 0; needIdx < needsList.size(); needIdx++) {
            needToColMap.put(needsList.get(needIdx), needIdx);
        }
        matrix = gnMat.matrix;

        hasGoalCat = new HashMap();
        hasGoalCat.put(Question.GOAL_MOBILITY, majorGoalsList.get(0).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_SAFETY, majorGoalsList.get(1).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_PROD, majorGoalsList.get(2).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_REG, majorGoalsList.get(3).responseIdxProperty());
        hasGoalCat.put(Question.GOAL_TRAVELER_INFO, majorGoalsList.get(4).responseIdxProperty());

        connectNeedsProperties();
    }

    private void connectNeedsProperties() {
        for (Need n : needsList) {
            n.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                    updateTopGoals();
                }
            });
        }
        IntegerBinding ibMob = new IntegerBinding() {

            {
                for (Need n : getGoalListByType(Question.GOAL_MOBILITY)) {
                    super.bind(n.selectedProperty());
                }
            }

            @Override
            protected int computeValue() {
                for (Need n : getGoalListByType(Question.GOAL_MOBILITY)) {
                    if (n.isSelected()) {
                        return 1;
                    }
                }
                return 0;
            }
        };
        hasGoalCat.get(Question.GOAL_MOBILITY).bind(ibMob);
        IntegerBinding ibProd = new IntegerBinding() {

            {
                for (Need n : getGoalListByType(Question.GOAL_PROD)) {
                    super.bind(n.selectedProperty());
                }
            }

            @Override
            protected int computeValue() {
                for (Need n : getGoalListByType(Question.GOAL_PROD)) {
                    if (n.isSelected()) {
                        return 1;
                    }
                }
                return 0;
            }
        };
        hasGoalCat.get(Question.GOAL_PROD).bind(ibProd);
        IntegerBinding ibReg = new IntegerBinding() {

            {
                for (Need n : getGoalListByType(Question.GOAL_REG)) {
                    super.bind(n.selectedProperty());
                }
            }

            @Override
            protected int computeValue() {
                for (Need n : getGoalListByType(Question.GOAL_REG)) {
                    if (n.isSelected()) {
                        return 1;
                    }
                }
                return 0;
            }
        };
        hasGoalCat.get(Question.GOAL_REG).bind(ibReg);
        IntegerBinding ibSafety = new IntegerBinding() {

            {
                for (Need n : getGoalListByType(Question.GOAL_SAFETY)) {
                    super.bind(n.selectedProperty());
                }
            }

            @Override
            protected int computeValue() {
                for (Need n : getGoalListByType(Question.GOAL_SAFETY)) {
                    if (n.isSelected()) {
                        return 1;
                    }
                }
                return 0;
            }
        };
        hasGoalCat.get(Question.GOAL_SAFETY).bind(ibSafety);
        IntegerBinding ibTI = new IntegerBinding() {

            {
                for (Need n : getGoalListByType(Question.GOAL_TRAVELER_INFO)) {
                    super.bind(n.selectedProperty());
                }
            }

            @Override
            protected int computeValue() {
                for (Need n : getGoalListByType(Question.GOAL_TRAVELER_INFO)) {
                    if (n.isSelected()) {
                        return 1;
                    }
                }
                return 0;
            }
        };
        hasGoalCat.get(Question.GOAL_TRAVELER_INFO).bind(ibTI);

    }

    private void loadDefaultV2() {
        JSONObject json = GoalNeedsMatrix.loadJSON();
        JSONArray jArr = (JSONArray) json.get("Goals Matrix");
        for (int qIdx = 0; qIdx < jArr.size(); qIdx++) {
            JSONObject currQ = (JSONObject) jArr.get(qIdx);
            JSONArray scores = (JSONArray) currQ.get("Scores");
            int colIdx = 0;
            for (int scoreIdx = 0; scoreIdx < scores.size(); scoreIdx++) {
                JSONObject currScore = (JSONObject) scores.get(scoreIdx);
                if (currScore.getOrDefault("Category", "").toString().trim().equalsIgnoreCase("")) {
                    continue;
                }
                int currScoreVal = 0;
                try {
                    currScoreVal = Integer.parseInt(currScore.getOrDefault("Score", "0").toString());
                } catch (NumberFormatException e) {
                    // do nothing, default value of 0 is used
                }
                matrix[qIdx][colIdx] = currScoreVal;
                colIdx++;
            }
        }
    }

    public static JSONObject loadJSON() {
        JSONParser parser = new JSONParser();
        JSONObject returnJSON = null;
        try {
//            File customMatrix = new File(MainController.getScoringMatrixFolder() + "goalNeedsCustomMatrix.json");
//            File defaultMatrix = new File(MainController.getScoringMatrixFolder() + "goalNeedsDefaultMatrix.json");
            Path scoringMatrixFolder = MainController.getScoringMatrixFolder();
            File customMatrix = scoringMatrixFolder.resolve("goalNeedsCustomMatrix.json").toFile();
            File defaultMatrix = scoringMatrixFolder.resolve("goalNeedsDefaultMatrix.json").toFile();
            if (customMatrix.exists()) {
                returnJSON = (JSONObject) parser.parse(new FileReader(customMatrix));
            } else {
                returnJSON = (JSONObject) parser.parse(new FileReader(defaultMatrix));
            }
//            System.out.println(returnJSON.toJSONString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return returnJSON;
    }

    public void computeScores() {
        //for (int nIdx = 0; nIdx < needsList.size(); nIdx++) {
        for (Need n : needsList) {
            int scoreCounter = 0;
            for (Question q : qList) {
                if (q.getResponseIdx() == 1) {
                    scoreCounter += matrix[qToRowMap.get(q)][needToColMap.get(n)];
                }
            }
            n.setScore(scoreCounter);
        }

        //numMob = numProd = numReg = numSafety = numTravInfo = 0;
        updateTopGoals();
    }

    public void updateTopGoals() {
        // Finding Top Scores
        int topMobScore = 0;
        Need topMobNeed = null;
        int topProdScore = 0;
        Need topProdNeed = null;
        int topRegScore = 0;
        Need topRegNeed = null;
        int topSafetyScore = 0;
        Need topSafetyNeed = null;
        int topTIScore = 0;
        Need topTINeed = null;
        for (Need n : needsList) {
            switch (n.getGoal()) {
                case Question.GOAL_MOBILITY:
                    //numMob++;
                    if (n.isSelected() && n.getScore() > topMobScore) {
                        topMobScore = n.getScore();
                        topMobNeed = n;
                    }
                    break;
                case Question.GOAL_PROD:
                    //numProd++;
                    if (n.isSelected() && n.getScore() > topProdScore) {
                        topProdScore = n.getScore();
                        topProdNeed = n;
                    }
                    break;
                case Question.GOAL_REG:
                    //numReg++;
                    if (n.isSelected() && n.getScore() > topRegScore) {
                        topRegScore = n.getScore();
                        topRegNeed = n;
                    }
                    break;
                case Question.GOAL_SAFETY:
                    //numSafety++;
                    if (n.isSelected() && n.getScore() > topSafetyScore) {
                        topSafetyScore = n.getScore();
                        topSafetyNeed = n;
                    }
                    break;
                case Question.GOAL_TRAVELER_INFO:
                    //numTravInfo++;
                    if (n.isSelected() && n.getScore() > topTIScore) {
                        topTIScore = n.getScore();
                        topTINeed = n;
                    }
                    break;

            }
        }
        this.topMobilityGoal.set(topMobNeed != null ? topMobNeed.getDescription() : "No mobility goals selected by user (See Goal Wizard in Step 1).");
        this.topProdGoal.set(topProdNeed != null ? topProdNeed.getDescription() : "No productivity goals selected by user(See Goal Wizard in Step 1).");
        this.topRegGoal.set(topRegNeed != null ? topRegNeed.getDescription() : "No regulatory goals selected by user(See Goal Wizard in Step 1).");
        this.topSafetyGoal.set(topSafetyNeed != null ? topSafetyNeed.getDescription() : "No safety goals selected by user(See Goal Wizard in Step 1).");
        this.topTIGoal.set(topTINeed != null ? topTINeed.getDescription() : "No traveler goals selected by user(See Goal Wizard in Step 1).");
    }

    public Node createSummaryTable() {
        computeScores();

        TableView<Need> summary = new TableView<>();
        summary.getStyleClass().add("step-selection-table");
        summary.setEditable(true);
        summary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Need, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("goal"));
        catCol.setPrefWidth(175);
        catCol.setMaxWidth(175);
        catCol.setMinWidth(175);
        catCol.getStyleClass().add("col-style-center");

        TableColumn<Need, String> recCol = new TableColumn<>("Subcategory"); // heading changed in accordance to recommendations from tool updates spreadsheet
        recCol.setEditable(false);
        recCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        recCol.setCellFactory(new Callback<TableColumn<Need, String>, TableCell<Need, String>>() {
            @Override
            public TableCell<Need, String> call(TableColumn<Need, String> tc) {
                return new TextFieldTableCell<Need, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            setText(item);
                            for (Need n : needsList) {
                                if (n.getDescription().equalsIgnoreCase(item)) {
                                    if (n.hasHL) {
                                        setGraphic(n.hl);
                                        this.setContentDisplay(ContentDisplay.RIGHT);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                };
            }
        });

        TableColumn<Need, Integer> scoreCol = new TableColumn<>(SCORE_COL_NAME);
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setPrefWidth(SCORE_COL_WIDTH);
        scoreCol.setMaxWidth(SCORE_COL_WIDTH);
        scoreCol.setMinWidth(SCORE_COL_WIDTH);
        scoreCol.setEditable(false);
        scoreCol.getStyleClass().add("col-style-center");
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer integer) {
                return Need.getScoreString(integer);
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        }));
//        scoreCol.setCellFactory(tc -> {
//            final TextFieldTableCell<Need, String> tfe = new TextFieldTableCell<>();
//            tfe.textProperty().addListener((ov, oldVal, newVal) -> {
//                if (newVal != null) {
//                    try {
//                        int score = Integer.parseInt(newVal);
//                        tfe.setText(Need.getScoreString(score));
//                        System.out.println(Need.getScoreString(score));
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            return tfe;
//        });

        TableColumn<Need, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectedCol));
        selectedCol.setPrefWidth(85);
        selectedCol.setMaxWidth(85);
        selectedCol.setMinWidth(85);

        summary.getColumns().addAll(catCol, recCol, scoreCol, selectedCol);

        summary.setItems(Need.getSortedNeedsList(needsList));

        //summary.getSortOrder().setAll(catCol, scoreCol);
        summary.setPlaceholder(new Label("The \"User Needs\" step must be completed to view."));

        BorderPane bPane = new BorderPane();
        bPane.setTop(NodeFactory.createFormattedLabel("Use the checkboxes in the far right column to select goals for the project. "
                + "These goals and their associated scores are based on user input from prior steps. "
                + "Specific goals should be selected for emphasis for this project based on "
                + "both the score recommendations and the analyst's judgement.", "opt-pane-title"));
        bPane.setCenter(summary);
        return bPane;
    }

    public Node createSelectedGoalsNode() {
        computeScores();
        final TableView<Need> summary = new TableView();
        summary.getStyleClass().add("step-summary-table");

        summary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn indexCol = new TableColumn("#");
        indexCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<QuestionYN, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<QuestionYN, String> p) {
                return new ReadOnlyObjectWrapper(Integer.toString(summary.getItems().indexOf(p.getValue()) + 1));
            }
        });
        indexCol.setPrefWidth(35);
        indexCol.setMaxWidth(35);
        indexCol.setMinWidth(35);
        indexCol.getStyleClass().add("col-style-center-bold");

        TableColumn catCol = new TableColumn("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("goal"));
        catCol.setPrefWidth(175);
        catCol.setMaxWidth(175);
        catCol.setMinWidth(175);
        catCol.getStyleClass().add("col-style-center");
        //catCol.setSortType(SortType.ASCENDING);

        TableColumn recCol = new TableColumn("Selected Goals");
        recCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn scoreCol = new TableColumn(SCORE_COL_NAME);
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setPrefWidth(SCORE_COL_WIDTH);
        scoreCol.setMaxWidth(SCORE_COL_WIDTH);
        scoreCol.setMinWidth(SCORE_COL_WIDTH);
        scoreCol.getStyleClass().add("col-style-center");
        scoreCol.setEditable(false);
        scoreCol.setCellFactory(new Callback<TableColumn<Need, String>, TableCell<Need, String>>() {
            @Override
            public TableCell<Need, String> call(TableColumn<Need, String> tc) {
                final TextFieldTableCell<Need, String> tfe = new TextFieldTableCell();
                tfe.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
                        if (newVal != null) {
                            try {
                                int score = Integer.parseInt(newVal);
                                tfe.setText(Need.getScoreString(score));
                            } catch (NumberFormatException e) {

                            }
                        }
                    }
                });
                return tfe;
            }
        });

        summary.getColumns().addAll(catCol, recCol, scoreCol);

        FilteredList<Need> filteredNeeds = needsList.filtered(new Predicate<Need>() {
            @Override
            public boolean test(Need nn) {
                return nn.isSelected();
            }
        });
        //summary.setItems(needsList);
        summary.setItems(Need.getSortedNeedsList(filteredNeeds));

        //summary.getSortOrder().setAll(catCol, scoreCol);
        summary.setPlaceholder(new Label("At least one goal must be selected in the previous step."));

        return summary;
    }

    public ObservableList<Need> getGoalListByType(final String goalType) {
        //computeScores();
//        final ArrayList<Need> al = new ArrayList();
//        for (Need n : needsList) {
//            if (n.getGoal().equalsIgnoreCase(goalType) && !n.isPlaceholder && n.isSelected()) {
//                al.add(n);
//            }
//        }
        FilteredList al = needsList.filtered(new Predicate<Need>() {
            @Override
            public boolean test(Need nn) {
                return nn.getGoal().equalsIgnoreCase(goalType);
            }
        });

        if (al.isEmpty()) {
            return FXCollections.observableArrayList(new Need(goalType, "No selected " + goalType + " goals."));
        }
        return al;
    }

    private final StringProperty topMobilityGoal = new SimpleStringProperty();

    public String getTopMobilityGoal() {
        return topMobilityGoal.get();
    }

    public void setTopMobilityGoal(String value) {
        topMobilityGoal.set(value);
    }

    public StringProperty topMobilityGoalProperty() {
        return topMobilityGoal;
    }
    private final StringProperty topProdGoal = new SimpleStringProperty();

    public String getTopProdGoal() {
        return topProdGoal.get();
    }

    public void setTopProdGoal(String value) {
        topProdGoal.set(value);
    }

    public StringProperty topProdGoalProperty() {
        return topProdGoal;
    }
    private final StringProperty topRegGoal = new SimpleStringProperty();

    public String getTopRegGoal() {
        return topRegGoal.get();
    }

    public void setTopRegGoal(String value) {
        topRegGoal.set(value);
    }

    public StringProperty topRegGoalProperty() {
        return topRegGoal;
    }
    private final StringProperty topSafetyGoal = new SimpleStringProperty();

    public String getTopSafetyGoal() {
        return topSafetyGoal.get();
    }

    public void setTopSafetyGoal(String value) {
        topSafetyGoal.set(value);
    }

    public StringProperty topSafetyGoalProperty() {
        return topSafetyGoal;
    }
    private final StringProperty topTIGoal = new SimpleStringProperty();

    public String getTopTIGoal() {
        return topTIGoal.get();
    }

    public void setTopTIGoal(String value) {
        topTIGoal.set(value);
    }

    public StringProperty topTIGoalProperty() {
        return topTIGoal;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeInt(qList.size());
        for (int i = 0; i < qList.size(); i++) {
            s.writeObject(qList.get(i));
        }
        s.writeInt(needsList.size());
        for (int i = 0; i < needsList.size(); i++) {
            s.writeObject(needsList.get(i));
        }

        s.writeObject(matrix);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.qList = FXCollections.observableArrayList();
        int numQ = s.readInt();
        qList = FXCollections.observableArrayList();
        for (int i = 0; i < numQ; i++) {
            qList.add((QuestionYN) s.readObject());
        }

        this.needsList = FXCollections.observableArrayList();
        int numNeeds = s.readInt();
        needsList = FXCollections.observableArrayList();
        for (int i = 0; i < numNeeds; i++) {
            needsList.add((Need) s.readObject());
        }

        matrix = (int[][]) s.readObject();
    }

    public static final String SCORE_COL_NAME = "Ranking";
    public static final int SCORE_COL_WIDTH = 200;
    public static final String ZERO_SCORE_TXT = "N/A";
    public static final String LOW_CAT_LABEL = "1"; // formerly "Low" changed for comments from spreadsheet
    public static final String MED_CAT_LABEL = "2"; // formerly "Medium" changed for comments from spreadsheet
    public static final String HIGH_CAT_LABEL = "3"; // formerly "High" changed for comments from spreadsheet
    public static final int LOW_CAT_MIN = 1;
    public static final int LOW_CAT_MAX = 4;
    public static final int MED_CAT_MIN = 5;
    public static final int MED_CAT_MAX = 8;
    public static final int HIGH_CAT_MIN = 9;

}
