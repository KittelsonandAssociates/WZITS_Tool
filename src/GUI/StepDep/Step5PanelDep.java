/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.StepDep;

import GUI.Helper.IconHelper;
import GUI.Helper.NodeFactory;
import GUI.MainController;
import GUI.Tables.Step5TableHelper;
import core.Project;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 *
 * @author ltrask
 */
public class Step5PanelDep extends BorderPane {

    private final MainController control;

    private final int stepIndex = 4;

    private final String stepTitle = "System Deployment";

    private final VBox mainVBox = new VBox();

    private final GridPane allSubStepsPane = new GridPane();

    private final GridPane stepIntroGrid = new GridPane();
    private final BorderPane sysPlansPane = new BorderPane();
    private final BorderPane schedulePane = new BorderPane();
    private final BorderPane acceptanceTrainingPane = new BorderPane();
    private final BorderPane deploymentIssuesPane = new BorderPane();
    private final BorderPane stepReportPane = new BorderPane();

    public Step5PanelDep(MainController control) {

        this.control = control;

        mainVBox.setFillWidth(true);
        Label startLabel = new Label("Step");
        startLabel.setWrapText(true);
        startLabel.setAlignment(Pos.BOTTOM_CENTER);
        startLabel.setTextAlignment(TextAlignment.CENTER);
        startLabel.setMaxHeight(MainController.MAX_HEIGHT);
        startLabel.setMaxWidth(MainController.MAX_WIDTH);
        Label infoLabel = new Label("5");
        infoLabel.setMaxHeight(MainController.MAX_HEIGHT);
        infoLabel.setMaxWidth(MainController.MAX_WIDTH);
        infoLabel.setAlignment(Pos.TOP_CENTER);
        Label instructionLabel = new Label(stepTitle);
        instructionLabel.setWrapText(true);
        instructionLabel.setTextAlignment(TextAlignment.CENTER);
        instructionLabel.setMaxHeight(MainController.MAX_HEIGHT);
        instructionLabel.setMaxWidth(MainController.MAX_WIDTH);
        instructionLabel.setAlignment(Pos.CENTER);
        startLabel.getStyleClass().add("launch-title-label-top");
        infoLabel.getStyleClass().add("launch-title-label-bottom");
        instructionLabel.getStyleClass().add("intro-instructions");

        DoubleBinding widthBinding = new DoubleBinding() {
            {
                super.bind(widthProperty());
            }

            @Override
            protected double computeValue() {
                //return Math.max(widthProperty().get() * 0.70, 700);
                return (widthProperty().get() / (Project.NUM_SUB_STEPS[1] + 2) - 150); // 0.9
                //return (widthProperty().get()) * 0.2;
            }
        };

        DoubleBinding heightBinding = new DoubleBinding() {
            {
                super.bind(heightProperty());
            }

            @Override
            protected double computeValue() {
                //return Math.max(heightProperty().get() * 0.35, 150);
                return heightProperty().get() * 0.35; // 0.35
            }
        };

        ImageView figStep = new ImageView(IconHelper.FIG_FLOW_STEP_5);
        //figStep1.setFitWidth(1500);
        figStep.fitWidthProperty().bind(widthBinding);
        figStep.fitHeightProperty().bind(heightBinding);
        figStep.setPreserveRatio(true);
        figStep.setSmooth(true);
        figStep.setCache(true);

        stepIntroGrid.add(startLabel, 0, 0);
        stepIntroGrid.add(infoLabel, 0, 1);
        stepIntroGrid.add(instructionLabel, 1, 1);
        stepIntroGrid.add(figStep, 1, 0);
        stepIntroGrid.setStyle("-fx-background-color: white");

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        row1.setVgrow(Priority.ALWAYS);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        row2.setVgrow(Priority.ALWAYS);
        stepIntroGrid.getRowConstraints().addAll(row1, row2);
        ColumnConstraints colConst1 = new ColumnConstraints(150, 150, 150);
        ColumnConstraints colConst2 = new ColumnConstraints(1, 150, MainController.MAX_WIDTH, Priority.ALWAYS, HPos.CENTER, true);
        stepIntroGrid.getColumnConstraints().addAll(colConst1, colConst2);
        GridPane.setHgrow(instructionLabel, Priority.ALWAYS);

        int subStepIndex = 1;
        // Initial Applications Questions Panel
        sysPlansPane.setTop(NodeFactory.createFormattedLabel(Project.STEP_NAMES[stepIndex][subStepIndex++], "substep-title-label"));
        sysPlansPane.setCenter(Step5TableHelper.createSysPlansNode(control.getProject()));
        sysPlansPane.setBottom(NodeFactory.createFormattedLabel("", "substep-title-label"));

        // Application Wizard Summary Panel
        schedulePane.setTop(NodeFactory.createFormattedLabel(Project.STEP_NAMES[stepIndex][subStepIndex++], "substep-title-label"));
        schedulePane.setCenter(Step5TableHelper.createSchedulingNode(control.getProject()));
        schedulePane.setBottom(NodeFactory.createFormattedLabel("", "substep-title-label"));

        // Benefits Questions Panel
        acceptanceTrainingPane.setTop(NodeFactory.createFormattedLabel(Project.STEP_NAMES[stepIndex][subStepIndex++], "substep-title-label"));
        acceptanceTrainingPane.setCenter(Step5TableHelper.createAcceptanceTrainingNode(control.getProject()));
        acceptanceTrainingPane.setBottom(NodeFactory.createFormattedLabel("", "substep-title-label"));

        // Costs Questions Panel
        deploymentIssuesPane.setTop(NodeFactory.createFormattedLabel(Project.STEP_NAMES[stepIndex][subStepIndex++], "substep-title-label"));
        deploymentIssuesPane.setCenter(Step5TableHelper.createDeploymentIssuesNode(control.getProject()));
        deploymentIssuesPane.setBottom(NodeFactory.createFormattedLabel("", "substep-title-label"));

        // Step Report Pane
        stepReportPane.setTop(NodeFactory.createFormattedLabel("Report: " + stepTitle, "substep-title-label"));
        Label tempSummaryLabel = new Label("Step 5 Summary Panel Under Development");
        tempSummaryLabel.setStyle("-fx-font-size: 24; -fx-text-wrap: true;");
        stepReportPane.setCenter(tempSummaryLabel);
        stepReportPane.setBottom(NodeFactory.createFormattedLabel("", "substep-title-label"));

        mainVBox.getChildren().addAll(allSubStepsPane);
        int i = 0;
        allSubStepsPane.add(stepIntroGrid, i++, 0);
        allSubStepsPane.add(sysPlansPane, i++, 0);
        allSubStepsPane.add(schedulePane, i++, 0);
        allSubStepsPane.add(acceptanceTrainingPane, i++, 0);
        allSubStepsPane.add(deploymentIssuesPane, i++, 0);
        allSubStepsPane.add(stepReportPane, i++, 0);

        int numPanes = getNumSubSteps() + 2;
        for (int colIdx = 0; colIdx < numPanes; colIdx++) {
            ColumnConstraints tcc = new ColumnConstraints();
            tcc.setPercentWidth(100.0 / numPanes);
            allSubStepsPane.getColumnConstraints().add(tcc);
        }

        GridPane.setVgrow(stepIntroGrid, Priority.ALWAYS);
        GridPane.setVgrow(sysPlansPane, Priority.ALWAYS);
        GridPane.setVgrow(schedulePane, Priority.ALWAYS);
        GridPane.setVgrow(acceptanceTrainingPane, Priority.ALWAYS);
        GridPane.setVgrow(deploymentIssuesPane, Priority.ALWAYS);
        GridPane.setVgrow(stepReportPane, Priority.ALWAYS);
        VBox.setVgrow(allSubStepsPane, Priority.ALWAYS);
        this.setCenter(mainVBox);

        setupActionListeners();
        setupPropertyBindings();

    }

    private void setupActionListeners() {

    }

    private void setupPropertyBindings() {
        this.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldWidth, Number newWidth) {
                //System.out.println("Step 5 Width Resized");
                if (allSubStepsPane != null && allSubStepsPane.isVisible()) {
                    allSubStepsPane.setMinWidth((getNumSubSteps() + 2) * (control.getAppWidth() - 220));
                    allSubStepsPane.setMaxWidth((getNumSubSteps() + 2) * (control.getAppWidth() - 220));
                    moveScreen((getActiveSubStep() + 1) * stepIntroGrid.getWidth(), 0, false);
                }
            }
        });

        control.activeStepProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                if (newVal.intValue() > stepIndex) {
                    selectSubStep(Project.NUM_SUB_STEPS[stepIndex], false);
                } else if (newVal.intValue() < stepIndex) {
                    selectSubStep(-1, false);
                } else {
                    selectSubStep(control.getActiveSubStep(stepIndex));
                }
            }
        });

        control.activeSubStepProperty(stepIndex).addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                selectSubStep(getActiveSubStep());
                control.getProject().setSubStepStarted(stepIndex, getActiveSubStep(), true);
                control.getProject().setSubStepComplete(stepIndex, getActiveSubStep() - 1, true);

                control.checkProceed();
            }
        });
    }

    private int getActiveSubStep() {
        return control.getActiveSubStep(stepIndex);
    }

    private int getNumSubSteps() {
        return Project.NUM_SUB_STEPS[stepIndex];
    }

    public void setViewWidth(double viewWidth) {
        if (allSubStepsPane != null) {
            allSubStepsPane.setMinWidth((getNumSubSteps() + 2) * (control.getAppWidth() - 220));
            allSubStepsPane.setMaxWidth((getNumSubSteps() + 2) * (control.getAppWidth() - 220));
            moveScreen((getActiveSubStep() + 1) * stepIntroGrid.getWidth(), 0, false);
        }
    }

    private void selectSubStep(int subStepIndex) {
        selectSubStep(subStepIndex, true);
    }

    private void selectSubStep(int subStepIndex, boolean animated) {
        moveScreen((subStepIndex + 1) * stepIntroGrid.getWidth(), 0, animated);
    }

    private void moveScreen(double toX, double toY) {
        moveScreen(toX, toY, true);
    }

    private void moveScreen(double toX, double toY, boolean animated) {
        if (animated) {
            TranslateTransition moveMe = new TranslateTransition(Duration.seconds(0.1), allSubStepsPane);
            moveMe.setToX(-1 * toX);
            moveMe.setToY(toY);
            moveMe.play();
        } else {
            allSubStepsPane.setTranslateX(-1 * toX);
        }
    }

}
