package com.example.rocketmissionsimulatormodule;
import javafx.scene.layout.BorderPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class RocketMissionSimulatorApplication extends Application {
    @Override
    public void start(Stage stage) {

        BorderPane mainPane = new BorderPane();
        Pane simulationPane = new Pane();
        VBox telemetryVBox= new VBox();
        VBox rightVBox = new VBox();
        VBox missionVBox = new VBox();
        Insets insets = new Insets(15);
        TextArea missionLogArea = new TextArea();

        rightVBox.getChildren().addAll(telemetryVBox, missionVBox);
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);
        mainPane.setRight(rightVBox);

        Label telemetryTitle = new Label("Telemetry");
        Label altitudeLabel = new Label("Altitude: 0 km");
        Label velocityLabel = new Label("Velocity: 0 m/s");
        Label fuelLabel = new Label("Fuel: 100%");
        Label throttleLabel = new Label("Throttle: 0%");
        Label orientationLabel = new Label("Orientation: 0°");


        simulationPane.setPrefSize(800, 500);
        simulationPane.setStyle("-fx-background-color: #101D35;");
        mainPane.setCenter(simulationPane);


        telemetryVBox.getChildren().addAll(telemetryTitle, altitudeLabel, velocityLabel, fuelLabel, throttleLabel, orientationLabel);
        telemetryVBox.setPadding(insets);
        telemetryVBox.setSpacing(10);
        telemetryVBox.setStyle("-fx-background-color: #E2E8F0;");




        Label missionTitle = new Label("Mission / Events");
        Label statusLabel = new Label("Status: Pre-launch");
        Label activeEventLabel = new Label("Active Event: None");
        Label objectiveLabel = new Label("Objective: Deploy satellite");

        missionVBox.getChildren().addAll(missionTitle, statusLabel, activeEventLabel, objectiveLabel);
        missionVBox.setPadding(insets);
        missionVBox.setSpacing(10);
        missionVBox.setStyle("-fx-background-color: #FCE8D5;");


        missionLogArea.setEditable(false);
        missionLogArea.setPrefRowCount(5);
        missionLogArea.setText("[00:00] Mission initialized");
        missionLogArea.setStyle(
                "-fx-control-inner-background: #4c5057;"
                        + "-fx-text-fill: #FFFFFF;"
        );
        mainPane.setBottom(missionLogArea);

        Scene scene = new Scene(mainPane, 1200, 750);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();
    }
}
