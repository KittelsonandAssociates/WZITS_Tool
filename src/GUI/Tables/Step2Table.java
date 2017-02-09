/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Tables;

import core.Application;
import core.Question;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author ltrask
 */
public class Step2Table extends TableView {
    
    
    public static TableView createPageTable(int page, int questionsPerPage) {


        int startRow = page * questionsPerPage;
        int endRow = Math.min((page + 1) * questionsPerPage, TableHelper.getNumberOfQuestionsByStep(0));
        
        TableView<Question> table = new TableView();
        table.setEditable(true);
        
        // Setting up table columns
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn indexCol = new TableColumn("#");
        indexCol.setCellValueFactory(new PropertyValueFactory<Question, Integer>("idx"));
        indexCol.setPrefWidth(25);
        indexCol.setMaxWidth(25);
        indexCol.setMinWidth(25);
        indexCol.getStyleClass().add("col-style-center");

        TableColumn goalCol = new TableColumn("WZITS Goal Category");
        goalCol.setCellValueFactory(new PropertyValueFactory<Question, String>("goal"));
        goalCol.setPrefWidth(200);
        goalCol.setMaxWidth(200);
        goalCol.setMinWidth(200);
        goalCol.getStyleClass().add("col-style-center");

        TableColumn questionCol = new TableColumn("Input Question");
        questionCol.setCellValueFactory(new PropertyValueFactory<Question, String>("questionText"));

        TableColumn responseCol = new TableColumn("User Response");
        responseCol.setPrefWidth(150);
        responseCol.setMaxWidth(150);
        responseCol.setMinWidth(150);
        responseCol.setCellValueFactory(new PropertyValueFactory<Question, Integer>("responseIdx"));
//        responseCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(Question.yesNoConverter, FXCollections.observableArrayList(-1, 0, 1)));
//        responseCol.setOnEditCommit(new EventHandler<CellEditEvent<Question, Integer>>() {
//            @Override
//            public void handle(CellEditEvent<Question, Integer> t) {
//                ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setResponseIdx(t.getNewValue());
//            }
//        });

        TableColumn yesCol = new TableColumn("Yes");
        yesCol.setCellValueFactory(new PropertyValueFactory<Question, Integer>("responseIdx"));
        yesCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer idx) {
                return new SimpleBooleanProperty(idx == 1 ? Boolean.TRUE : Boolean.FALSE);
            }
        }));
        yesCol.setOnEditCommit(new EventHandler<CellEditEvent<Question, Integer>>() {
            @Override
            public void handle(CellEditEvent<Question, Integer> t) {
                ((Question) t.getTableView().getItems().get(t.getTablePosition().getRow())).setResponseIdx(t.getNewValue());
            }
        });
        TableColumn noCol = new TableColumn("No");
        noCol.setCellValueFactory(new PropertyValueFactory<Question, Integer>("responseIdx"));
        noCol.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer idx) {
                return new SimpleBooleanProperty(idx == 0 ? Boolean.TRUE : Boolean.FALSE);
            }
        }));
        yesCol.setPrefWidth(75);
        yesCol.setMaxWidth(75);
        yesCol.setMinWidth(75);
        noCol.setPrefWidth(75);
        noCol.setMaxWidth(75);
        noCol.setMinWidth(75);
        responseCol.getColumns().addAll(yesCol, noCol);

        table.getColumns().addAll(indexCol, goalCol, questionCol, responseCol);
        
        //ObservableList<Question> stepQuestions = TableHelper.getStepQuestions(0);
        ObservableList<Question> stepQuestions = FXCollections.observableArrayList(TableHelper.getStepQuestions(0).subList(startRow, endRow));
        
        table.setItems(stepQuestions);

        return table;

    }

    public static TableView createSummaryTable() {
        TableView summary = new TableView();
        summary.getStyleClass().add("step-summary-table");

        summary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn appCol = new TableColumn("Recommended WZITS Applications");
        appCol.setCellValueFactory(new PropertyValueFactory<Application, String>("name"));

        TableColumn scoreCol = new TableColumn("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<Application, String>("score"));
        scoreCol.setPrefWidth(100);
        scoreCol.setMaxWidth(100);
        scoreCol.setMinWidth(100);
        scoreCol.getStyleClass().add("col-style-center");

        summary.getColumns().addAll(appCol, scoreCol);

        ObservableList<Application> stepSummary = TableHelper.getStepSummary(0);
        summary.setItems(stepSummary);
        return summary;
    }
    
    public static int getPageCount(int stepIdx, int questionsPerPage) {
        return Math.floorDiv(TableHelper.getNumberOfQuestionsByStep(0), questionsPerPage) + 1;
    }

    public static int getPageCount(int stepIdx) {
        return getPageCount(stepIdx, 10);
    }
}
