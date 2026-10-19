
package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.util.Mission;
import com.example.rocketmissionsimulatormodule.util.MissionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import java.util.function.Consumer;


public class MissionPane extends VBox {

    private Mission mission;

    private Button launchButton;
    private Label statusLabel;
    private Runnable onLaunch;

    private Button deployButton;
    private Runnable onDeploy;

    private ListView<String> eventOptionsList;
    private Consumer<Integer> onEventResponse;

    private Label activeEventLabel;


    public MissionPane(Mission mission) {
        this.mission = mission;

        initializePane();
        initializeLabels();
        initializeLaunchButton();
        initializeDeployButton();
        initializeEventConsole();
        initializeLayout();
    }

    private void initializePane() {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #FCE8D5;");
    }

    private void initializeLabels() {
        statusLabel = new Label(
                "Status: " + mission.getCurrentState().toString()
        );
    }

    private void initializeLaunchButton() {
        launchButton = new Button("Launch");
        launchButton.setPrefWidth(240);
        launchButton.setPrefHeight(45);
        launchButton.setMinHeight(40);
        launchButton.setDisable(false);

        launchButton.setOnAction(event -> {
            mission.launch();

            statusLabel.setText(
                    "Status: " + mission.getCurrentState().toString()
            );

            launchButton.setDisable(true);

            if (onLaunch != null) {
                onLaunch.run();
            }
        });
    }

    private void initializeDeployButton() {
        deployButton = new Button("Deploy Satellite");
        deployButton.setPrefWidth(240);
        deployButton.setPrefHeight(45);
        deployButton.setMinHeight(40);
        deployButton.setDisable(true);

        deployButton.setOnAction(event -> {
            if (onDeploy != null) {
                onDeploy.run();
            }
        });
    }






    private void initializeEventConsole() {
        eventOptionsList = new ListView<>();
        eventOptionsList.setPrefHeight(120);
        eventOptionsList.setStyle(
                "-fx-control-inner-background: #101820;"
                        + "-fx-background-color: #101820;"
                        + "-fx-text-fill: #c400ff;"
                        + "-fx-font-family: 'Monospaced';"
                        + "-fx-font-size: 13px;"
        );



        eventOptionsList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                int selectedIndex = eventOptionsList
                        .getSelectionModel()
                        .getSelectedIndex();

                if (selectedIndex >= 0 && onEventResponse != null) {
                    onEventResponse.accept(selectedIndex);
                }

                event.consume();
            }
        });


        eventOptionsList.setDisable(true);
    }






    private void initializeLayout() {
        Label missionTitle = new Label("Mission / Events");
        activeEventLabel = new Label("Active Event: None");
        Label objectiveLabel = new Label("Objective: Deploy satellite");

        getChildren().addAll(
                missionTitle,
                statusLabel,
                activeEventLabel,
                objectiveLabel,
                eventOptionsList,
                launchButton,
                deployButton
        );
    }

    public void setOnLaunch(Runnable onLaunch) {
        this.onLaunch = onLaunch;
    }

    public void setOnDeploy(Runnable onDeploy) {
        this.onDeploy = onDeploy;
    }

    public void setOnEventResponse(Consumer<Integer> onEventResponse) {
        this.onEventResponse = onEventResponse;
    }

    public void updateMissionStatus() {
        Mission.MissionState state = mission.getCurrentState();

        switch (state) {
            case SUCCESS:
                statusLabel.setText("Status: Mission Successful!");
                break;

            case FAILURE:
                statusLabel.setText("Status: Mission Failed!");
                break;

            default:
                statusLabel.setText("Status: " + state);
                break;
        }

        deployButton.setDisable(state != Mission.MissionState.ORBIT);
    }



    public void displayMissionEvent(MissionEvent event) {
        activeEventLabel.setText("Active Event: " + event);

        eventOptionsList.getItems().clear();

        switch (event) {
            case ENGINE_OVERHEATING:
                eventOptionsList.getItems().addAll(
                        "1. Reduce engine throttle",
                        "2. Activate emergency cooling",
                        "3. Ignore warning"
                );
                break;
        }

        eventOptionsList.setDisable(false);

        //enables  console and selects the first response
        eventOptionsList.getSelectionModel().selectFirst();
    }


    public void clearMissionEvent() {
        activeEventLabel.setText("Active Event: None");

        eventOptionsList.getItems().clear();
        eventOptionsList.getSelectionModel().clearSelection();
        eventOptionsList.setDisable(true);
    }
}
