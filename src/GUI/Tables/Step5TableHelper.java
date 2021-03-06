/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Tables;

import GUI.Helper.NodeFactory;
import GUI.MainController;
import core.Project;
import core.QuestionYN;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

/**
 *
 * @author ltrask
 */
public class Step5TableHelper extends TableView {

    private static final int STEP_INDEX = 4;

    public static final int APP_WIZARD = 0;

    private static final String STEP2_TABLE_CSS = "step-one-table";

    public static Pane createSysPlansNode(Project proj) {
        //return TableHelper.createQuestionYNTable(proj.getQGen().qSysPlansList, new TableHelper.Options(STEP2_TABLE_CSS));
        return TableHelper.createCommentPageYNv2(proj.getQGen().qSysPlansList);
    }

    public static Pane createSchedulingNode(Project proj) {
        //return TableHelper.createQuestionYNTable(proj.getQGen().qSchedulingList, new TableHelper.Options(STEP2_TABLE_CSS));
        return TableHelper.createCommentPageYNv2(proj.getQGen().qSchedulingList);
    }

    public static Pane createAcceptanceTrainingNode(Project proj) {
        //return TableHelper.createQuestionYNTable(proj.getQGen().qAcceptanceTrainingList, new TableHelper.Options(STEP2_TABLE_CSS));
        return TableHelper.createCommentPageYNv2(proj.getQGen().qAcceptanceTrainingList);
    }

    public static Pane createDeploymentIssuesNode(Project proj) {
        //return TableHelper.createQuestionYNTable(proj.getQGen().qDeploymentIssuesList, new TableHelper.Options(STEP2_TABLE_CSS));
        return TableHelper.createCommentPageYNv2(proj.getQGen().qDeploymentIssuesList);
    }

    public static Node createStepSummary(MainController mc) {
        int lfs = 16;
        final Project p = mc.getProject();
        BorderPane bPane = new BorderPane();
        bPane.setMinWidth(1000);
        bPane.setMaxWidth(1000);
        bPane.setPrefWidth(1000);
        bPane.getStyleClass().add("fact-sheet-pane");
        bPane.setTop(NodeFactory.createFormattedLabel("Fact Sheet #7: WZITS System Deployment", "fact-sheet-title-large"));
        final GridPane infoGrid = new GridPane();
        int infoC1Width = 115;
        int rowIdx = 0;
        infoGrid.add(NodeFactory.createFormattedLabel("State Agency:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedLabel(p.getAgency(), "fact-sheet-label"), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Analyst:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedLabel(p.getAnalyst(), "fact-sheet-label"), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Date:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedLabel(p.getDateString(), "fact-sheet-label"), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Project Name:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedLabel(p.getName(), "fact-sheet-label"), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Project Description:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedDescLabel(p.getDescription(), "fact-sheet-description", lfs, 4), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Project Limits:", "fact-sheet-label-bold"), 0, rowIdx);
        infoGrid.add(NodeFactory.createFormattedDescLabel(p.getLimits(), "fact-sheet-description", lfs, 4), 1, rowIdx++);
        infoGrid.add(NodeFactory.createFormattedLabel("Project Website:", "fact-sheet-label-bold"), 0, rowIdx);
        Hyperlink projHL = new Hyperlink(p.getUrlLink());
        projHL.getStyleClass().add("fact-sheet-label-url");
        projHL.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                if (p.getUrlLink() != null) {
                    try {
                        Runtime.getRuntime().exec("cmd /c start " + p.getUrlLink());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        projHL.setMaxWidth(MainController.MAX_WIDTH);
        projHL.setMaxHeight(MainController.MAX_HEIGHT);
        infoGrid.add(projHL, 1, rowIdx++);

        ColumnConstraints igcc1 = new ColumnConstraints(infoC1Width, infoC1Width, infoC1Width, Priority.NEVER, HPos.LEFT, true);
        ColumnConstraints igcc2 = new ColumnConstraints(1, 100, MainController.MAX_WIDTH, Priority.ALWAYS, HPos.LEFT, true);
        infoGrid.getColumnConstraints().addAll(igcc1, igcc2);

        GridPane docGrid = new GridPane();
        docGrid.add(NodeFactory.createFormattedLabel(MainController.STEP_TITLES[STEP_INDEX], "fact-sheet-title-stake-grid"), 0, 0, 4, 1);
        rowIdx = 1;
        docGrid.add(NodeFactory.createFormattedLabel("Category", "fact-sheet-title-core-team"), 0, rowIdx, 1, 1);
        docGrid.add(NodeFactory.createFormattedLabel("Question", "fact-sheet-title-core-team"), 1, rowIdx, 1, 1);
        docGrid.add(NodeFactory.createFormattedLabel("Response", "fact-sheet-title-core-team"), 2, rowIdx, 1, 1);
        docGrid.add(NodeFactory.createFormattedLabel("Comment", "fact-sheet-title-core-team"), 3, rowIdx++, 1, 1);

        ArrayList<ObservableList<QuestionYN>> stepLists = new ArrayList();

        stepLists.add(p.getQGen().qSysPlansList);
        stepLists.add(p.getQGen().qSchedulingList);
        stepLists.add(p.getQGen().qAcceptanceTrainingList);
        stepLists.add(p.getQGen().qDeploymentIssuesList);

        ObservableList<QuestionYN> qList;

        for (int listIdx = 0; listIdx < stepLists.size(); listIdx++) {
            qList = stepLists.get(listIdx);
            docGrid.add(NodeFactory.createFormattedLabel(Project.STEP_NAMES[STEP_INDEX][listIdx + 1], "fact-sheet-label-center"), 0, rowIdx, 1, qList.size());
            for (int qIdx = 0; qIdx < qList.size(); qIdx++) {
                docGrid.add(NodeFactory.createFormattedLabel(qList.get(qIdx).getQuestionText(), "fact-sheet-label"), 1, rowIdx);
                docGrid.add(NodeFactory.createFormattedLabel(qList.get(qIdx).getAnswerString(), "fact-sheet-label-center"), 2, rowIdx);
                docGrid.add(NodeFactory.createFormattedLabel(qList.get(qIdx).getComment(), "fact-sheet-label"), 3, rowIdx++);
            }
        }

        docGrid.getColumnConstraints().add(new ColumnConstraints(infoC1Width, infoC1Width, infoC1Width, Priority.NEVER, HPos.CENTER, true));
        docGrid.getColumnConstraints().add(new ColumnConstraints(300, 300, 300, Priority.NEVER, HPos.CENTER, true));
        docGrid.getColumnConstraints().add(new ColumnConstraints(100, 100, 100, Priority.NEVER, HPos.CENTER, true));
        docGrid.getColumnConstraints().add(new ColumnConstraints(1, 200, MainController.MAX_WIDTH, Priority.ALWAYS, HPos.LEFT, true));

        VBox factSheetVBox = new VBox();
        factSheetVBox.getChildren().addAll(infoGrid, docGrid);
        ScrollPane sp = new ScrollPane();

        bPane.setCenter(factSheetVBox);
        sp.setContent(bPane);
        //System.out.println("Here");
        return sp;
    }

}
