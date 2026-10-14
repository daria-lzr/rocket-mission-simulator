
package com.example.rocketmissionsimulatormodule.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MissionPane extends VBox {

    public MissionPane() {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-background-color: #FCE8D5;");

        Label missionTitle = new Label("Mission / Events");
        Label statusLabel = new Label("Status: Pre-launch");
        Label activeEventLabel = new Label("Active Event: None");
        Label objectiveLabel = new Label("Objective: Deploy satellite");

        getChildren().addAll(
                missionTitle,
                statusLabel,
                activeEventLabel,
                objectiveLabel
        );
    }
}
