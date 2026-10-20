package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class FailureOverlay extends StackPane {

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

        Label failureDescription =
                new Label("CRITICAL ENGINE MALFUNCTION");

        failureDescription.setStyle(
                "-fx-text-fill: #FECACA;"
                        + "-fx-font-size: 14px;"
        );

        VBox failureMessage = new VBox(
                10, failureTitle, failureDescription
        );

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