package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RecoveryNotification extends VBox {

    private Label notificationHeader;
    private Label notificationSymbol;
    private Label notificationTitle;
    private Label notificationDescription;
    private VBox notificationMessageBox;

    public RecoveryNotification() {
        setupNotification();
    }

    private void setupNotification() {
        notificationHeader = new Label("SUCCESSFUL RECOVERY");
        notificationHeader.setStyle(
                "-fx-text-fill: #9BAEC8;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        notificationSymbol = new Label("❄");
        notificationSymbol.setStyle(
                "-fx-text-fill: #60A5FA;"
                        + "-fx-font-size: 24px;"
        );

        notificationTitle = new Label("EMERGENCY COOLING");
        notificationTitle.setStyle(
                "-fx-text-fill: #93C5FD;"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        notificationDescription =
                new Label("Engine temperature stabilized");

        notificationDescription.setStyle(
                "-fx-text-fill: #BFDBFE;"
                        + "-fx-font-size: 12px;"
        );

        HBox titleRow = new HBox(
                10, notificationSymbol, notificationTitle
        );
        titleRow.setAlignment(Pos.CENTER);

        notificationMessageBox = new VBox(
                5, titleRow, notificationDescription
        );
        notificationMessageBox.setAlignment(Pos.CENTER);
        notificationMessageBox.setPadding(new Insets(15));

        notificationMessageBox.setStyle(
                "-fx-background-color: #102F50;"
                        + "-fx-border-color: #2875C0;"
                        + "-fx-border-width: 1;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-radius: 8;"
        );

        getChildren().addAll(
                notificationHeader,
                notificationMessageBox
        );

        setSpacing(10);
        setPadding(new Insets(12));
        setMaxWidth(350);

        setStyle(
                "-fx-background-color: #0D192B;"
                        + "-fx-background-radius: 12;"
        );

        setVisible(false);
        setMouseTransparent(true);
    }

    public void showNotification(boolean cooling) {
        configureNotification(cooling);

        setOpacity(0);
        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(
                Duration.seconds(0.3), this
        );
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition hold =
                new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(
                Duration.seconds(0.5), this
        );
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition animation =
                new SequentialTransition(fadeIn, hold, fadeOut);

        animation.setOnFinished(event -> setVisible(false));

        animation.play();
    }

    private void configureNotification(boolean cooling) {
        if (cooling) {
            notificationHeader.setText("SUCCESSFUL RECOVERY");
            notificationSymbol.setText("❄");
            notificationTitle.setText("EMERGENCY COOLING");
            notificationDescription.setText(
                    "Engine temperature stabilized"
            );

            setStyle(
                    "-fx-background-color: #0D192B;"
                            + "-fx-background-radius: 12;"
            );

            notificationMessageBox.setStyle(
                    "-fx-background-color: #102F50;"
                            + "-fx-border-color: #2875C0;"
                            + "-fx-border-width: 1;"
                            + "-fx-background-radius: 8;"
                            + "-fx-border-radius: 8;"
            );

            notificationHeader.setStyle("-fx-text-fill: #9BAEC8; -fx-font-size: 11px; -fx-font-weight: bold;");
            notificationSymbol.setStyle("-fx-text-fill: #60A5FA; -fx-font-size: 24px;");
            notificationTitle.setStyle("-fx-text-fill: #93C5FD; -fx-font-size: 16px; -fx-font-weight: bold;");
            notificationDescription.setStyle("-fx-text-fill: #BFDBFE; -fx-font-size: 12px;");

        } else {
            notificationHeader.setText("ENGINE RECOVERY");
            notificationSymbol.setText("◴");
            notificationTitle.setText("THROTTLE REDUCED");
            notificationDescription.setText(
                    "Engine operating at reduced power"
            );

            setStyle(
                    "-fx-background-color: #20190E;"
                            + "-fx-background-radius: 12;"
            );

            notificationMessageBox.setStyle(
                    "-fx-background-color: #45300F;"
                            + "-fx-border-color: #B7791F;"
                            + "-fx-border-width: 1;"
                            + "-fx-background-radius: 8;"
                            + "-fx-border-radius: 8;"
            );

            notificationHeader.setStyle("-fx-text-fill: #FCD34D; -fx-font-size: 11px; -fx-font-weight: bold;");
            notificationSymbol.setStyle("-fx-text-fill: #FBBF24; -fx-font-size: 24px;");
            notificationTitle.setStyle("-fx-text-fill: #FDE68A; -fx-font-size: 16px; -fx-font-weight: bold;");
            notificationDescription.setStyle("-fx-text-fill: #FCD34D; -fx-font-size: 12px;");
        }
    }
}