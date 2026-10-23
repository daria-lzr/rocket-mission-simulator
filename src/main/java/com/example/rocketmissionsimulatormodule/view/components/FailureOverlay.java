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

public class FailureOverlay extends StackPane {

    private final Button restartButton = new Button("Restart Mission");
    private final Button exitButton = new Button("Exit Application");

    public FailureOverlay() {
        setupOverlay();
    }

    private void setupOverlay() {
        Label failureTitle = new Label("FAILURE");
        failureTitle.setStyle(
                "-fx-text-fill: #FCA5A5;"
                        + "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );

        Label failureDescription = new Label("MISSION TERMINATED");

        failureDescription.setStyle(
                "-fx-text-fill: #FECACA;"
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

        VBox failureMessage = new VBox(14, failureTitle, failureDescription, actions);

        failureMessage.setAlignment(Pos.CENTER);
        failureMessage.setPadding(new Insets(25));

        failureMessage.setStyle(
                "-fx-background-color: #491C24;"
                        + "-fx-border-color: #B83B48;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
        );

        failureMessage.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        getChildren().add(failureMessage);

        setStyle(
                "-fx-background-color: rgba(100, 0, 0, 0.35);"
        );

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

    public void showFailure() {
        setOpacity(0);
        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(
                Duration.seconds(0.8),
                this
        );

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.play();
    }
}