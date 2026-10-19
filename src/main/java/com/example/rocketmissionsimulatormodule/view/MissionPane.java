
package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.util.Mission;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;



public class MissionPane extends VBox {
    private Mission mission;
    private Button launchButton;
    private Label statusLabel;
    private Runnable onLaunch;

    private Button deployButton;
    private Runnable onDeploy;

    public MissionPane(Mission mission) {
        this.mission = mission;

        launchButton = new Button("Launch");
        launchButton.setPrefWidth(240);
        launchButton.setPrefHeight(45);
        launchButton.setMinHeight(40);
        launchButton.setDisable(false);

        launchButton.setOnAction(event -> {
            this.mission.launch();

            statusLabel.setText("Status: " + mission.getCurrentState().toString());
            launchButton.setDisable(true);

            if (onLaunch != null) {//avoid NullPointerException if onLaunch callback was not set
                onLaunch.run();
            }
        });



        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #FCE8D5;");

        Label missionTitle = new Label("Mission / Events");
        statusLabel = new Label("Status: " + mission.getCurrentState().toString());
        Label activeEventLabel = new Label("Active Event: None");
        Label objectiveLabel = new Label("Objective: Deploy satellite");


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


        getChildren().addAll(
                missionTitle,
                statusLabel,
                activeEventLabel,
                objectiveLabel,
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



    public void updateMissionStatus() {
        statusLabel.setText("Status: " + mission.getCurrentState());

        deployButton.setDisable(
                mission.getCurrentState() != Mission.MissionState.ORBIT
        );
    }

}
