package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SuccessOverlay extends StackPane {

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

        VBox successMessage = new VBox(10, successTitle, successDescription);
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

    public void showSuccess() {
        setOpacity(0);
        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
