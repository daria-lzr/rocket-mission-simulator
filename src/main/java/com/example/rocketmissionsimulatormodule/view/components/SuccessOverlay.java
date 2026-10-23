package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SuccessOverlay extends StackPane {

    private final Button restartButton = new Button("Restart Mission");
    private final Button exitButton = new Button("Exit Application");

    public SuccessOverlay() {
        Label successTitle = new Label("SUCCESS");
        successTitle.setStyle(
                "-fx-text-fill: #86EFAC;"
                        + "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );

        Label successDescription = new Label("SATELLITE DEPLOYED SUCCESSFULLY");
        successDescription.setStyle(
                "-fx-text-fill: #BBF7D0;"
                        + "-fx-font-size: 14px;"
        );

        restartButton.setStyle(buttonStyle("#1C745B"));
        exitButton.setStyle(buttonStyle("#26343F"));
        restartButton.setPrefWidth(145);
        exitButton.setPrefWidth(145);
        restartButton.setPrefHeight(38);
        exitButton.setPrefHeight(38);

        HBox actions = new HBox(12, restartButton, exitButton);
        actions.setAlignment(Pos.CENTER);

        VBox successMessage = new VBox(14, successTitle, successDescription, actions);
        successMessage.setAlignment(Pos.CENTER);
        successMessage.setPadding(new Insets(25));
        successMessage.setStyle(
                "-fx-background-color: #163D30;"
                        + "-fx-border-color: #339C71;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
        );
        successMessage.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        getChildren().add(successMessage);

        setStyle("-fx-background-color: rgba(0, 70, 30, 0.35);");
        setVisible(false);
        setOpacity(0);
    }

    private static String buttonStyle(String background) {
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: white; -fx-font-size: 12px;"
                + "-fx-font-weight: bold; -fx-background-radius: 6;"
                + "-fx-cursor: hand;";
    }

    public void setOnRestart(Runnable action) {
        restartButton.setOnAction(event -> action.run());
    }

    public void setOnExit(Runnable action) {
        exitButton.setOnAction(event -> action.run());
    }

    public void showSuccess() {
        setOpacity(0);
        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
