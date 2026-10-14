package com.example.rocketmissionsimulatormodule;

import com.example.rocketmissionsimulatormodule.view.MissionLogPane;
import com.example.rocketmissionsimulatormodule.view.MissionPane;
import com.example.rocketmissionsimulatormodule.view.SimulationPane;
import com.example.rocketmissionsimulatormodule.view.TelemetryPane;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RocketMissionSimulatorApplication extends Application {



    @Override
    public void start(Stage stage) {
         Mission mission = new Mission();

        BorderPane mainPane = new BorderPane();


        SimulationPane simulationPane = new SimulationPane();
        VBox rightVBox = createRightSidebar(mission);
        MissionLogPane missionLogPane = new MissionLogPane();

        //assemble  main layout
        mainPane.setCenter(simulationPane);
        mainPane.setRight(rightVBox);
        mainPane.setBottom(missionLogPane);

        Scene scene = new Scene(mainPane, 1200, 750);
        stage.setTitle("Rocket Mission Simulator");
        stage.setScene(scene);
        stage.show();
    }



    private static VBox createRightSidebar(Mission mission) {
        VBox rightVBox = new VBox();
        rightVBox.setSpacing(10);
        rightVBox.setPrefWidth(280);

        rightVBox.getChildren().addAll(
                new TelemetryPane(),
                new MissionPane(mission)
        );

        return rightVBox;
    }

}