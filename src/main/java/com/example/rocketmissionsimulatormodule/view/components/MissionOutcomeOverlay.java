package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public abstract class MissionOutcomeOverlay extends StackPane {

    private final Label outcomeTitle;
    private final Label outcomeDescription;
    private final VBox outcomeMessage;

    private final Button restartButton = new Button("Restart Mission");
    private final Button exitButton = new Button("Exit Application");

    protected MissionOutcomeOverlay(String title, String description) {
        outcomeTitle = new Label(title);
        outcomeDescription = new Label(description);

        restartButton.setStyle(buttonStyle("#1C745B"));
        exitButton.setStyle(buttonStyle("#26343F"));
        restartButton.setPrefWidth(145);
        exitButton.setPrefWidth(145);
        restartButton.setPrefHeight(38);
        exitButton.setPrefHeight(38);

        HBox actions = new HBox(12, restartButton, exitButton);
        actions.setAlignment(Pos.CENTER);

        outcomeMessage = new VBox(14, outcomeTitle, outcomeDescription, actions);
        outcomeMessage.setAlignment(Pos.CENTER);
        outcomeMessage.setPadding(new Insets(25));
        outcomeMessage.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        getChildren().add(outcomeMessage);

        setVisible(false);
        setOpacity(0);
    }

    private static String buttonStyle(String background) {
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: white; -fx-font-size: 12px;"
                + "-fx-font-weight: bold; -fx-background-radius: 6;"
                + "-fx-cursor: hand;";
    }

    protected final void setOutcomeColors(String titleColor, String descriptionColor,
                                          String panelColor, String borderColor,
                                          String backgroundColor) {
        outcomeTitle.setStyle(
                "-fx-text-fill: " + titleColor + ";"
                        + "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );
        outcomeDescription.setStyle(
                "-fx-text-fill: " + descriptionColor + ";"
                        + "-fx-font-size: 14px;"
        );
        outcomeMessage.setStyle(
                "-fx-background-color: " + panelColor + ";"
                        + "-fx-border-color: " + borderColor + ";"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
        );
        setStyle("-fx-background-color: " + backgroundColor + ";");
    }

    protected abstract void applyOutcomeAppearance();

    public final void setOnRestart(Runnable action) {
        restartButton.setOnAction(event -> action.run());
    }

    public final void setOnExit(Runnable action) {
        exitButton.setOnAction(event -> action.run());
    }

    public final void showOutcome() {
        applyOutcomeAppearance();
        setOpacity(0);
        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
