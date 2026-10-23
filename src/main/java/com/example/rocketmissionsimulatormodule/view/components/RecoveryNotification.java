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


    private static final double NOTIFICATION_WIDTH = 600;
    private static final double NOTIFICATION_HEIGHT = 84;
    private static final double MESSAGE_WIDTH = 582;
    private static final double MESSAGE_HEIGHT = 56;

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
                2, titleRow, notificationDescription
        );
        notificationMessageBox.setAlignment(Pos.CENTER);
        notificationMessageBox.setPadding(new Insets(2, 12, 2, 12));
        notificationMessageBox.setMinSize(MESSAGE_WIDTH, MESSAGE_HEIGHT);
        notificationMessageBox.setPrefSize(MESSAGE_WIDTH, MESSAGE_HEIGHT);
        notificationMessageBox.setMaxSize(MESSAGE_WIDTH, MESSAGE_HEIGHT);

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

        setAlignment(Pos.CENTER);
        setFillWidth(false); // Do not stretch the inner box based on content.
        setSpacing(3);
        setPadding(new Insets(5, 9, 5, 9));
        setMinSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
        setPrefSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
        setMaxSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);

        notificationTitle.setWrapText(false);
        notificationDescription.setWrapText(false);

        setStyle(
                "-fx-background-color: #0D192B;"
                        + "-fx-background-radius: 12;"
        );

        setVisible(false);
        setMouseTransparent(true);
    }

    public void showNotification(boolean cooling) {
        configureNotification(cooling);
        playNotification();
    }

    public void showMaxQRecovery() {
        notificationHeader.setText("RECOVERY COMPLETE");
        notificationSymbol.setText("✓");
        notificationTitle.setText("AERODYNAMIC LOAD REDUCED");
        notificationDescription.setText("Throttle lowered to relieve structural stress");


        setStyle("-fx-background-color: #0B2430; -fx-background-radius: 12;");
        notificationMessageBox.setStyle("-fx-background-color: #103848;"
                + "-fx-border-color: #2FA3CA; -fx-border-width: 1;"
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        notificationHeader.setStyle("-fx-text-fill: #A7E8F7; -fx-font-size: 11px;"
                + "-fx-font-weight: bold;");
        notificationSymbol.setStyle("-fx-text-fill: #58D6F4; -fx-font-size: 24px;");
        notificationTitle.setStyle("-fx-text-fill: #9FE8FF; -fx-font-size: 16px;"
                + "-fx-font-weight: bold;");
        notificationDescription.setStyle("-fx-text-fill: #CCEFF7; -fx-font-size: 12px;");
        playNotification();
    }


    public void showPropellantRecovery() {
        notificationHeader.setText("RECOVERY COMPLETE");
        notificationSymbol.setText("✓");
        notificationTitle.setText("PROPELLANT STABILIZED");
        notificationDescription.setText("Slosh-damping controller engaged");

        setStyle("-fx-background-color: #21172F; -fx-background-radius: 12;");
        notificationMessageBox.setStyle("-fx-background-color: #35214D;"
                + "-fx-border-color: #9766C9; -fx-border-width: 1;"
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        notificationHeader.setStyle("-fx-text-fill: #DFC4FF; -fx-font-size: 11px;"
                + "-fx-font-weight: bold;");
        notificationSymbol.setStyle("-fx-text-fill: #C79BFF; -fx-font-size: 24px;");
        notificationTitle.setStyle("-fx-text-fill: #DFC4FF; -fx-font-size: 16px;"
                + "-fx-font-weight: bold;");
        notificationDescription.setStyle("-fx-text-fill: #F0E3FF; -fx-font-size: 12px;");
        playNotification();
    }


    public void showAttitudeRecovery() {
        notificationHeader.setText("RECOVERY COMPLETE");
        notificationSymbol.setText("✓");
        notificationTitle.setText("BACKUP RCS ACTIVE");
        notificationDescription.setText("Attitude stabilized for satellite deployment");

        setStyle("-fx-background-color: #111D38; -fx-background-radius: 12;");
        notificationMessageBox.setStyle("-fx-background-color: #1B2C52;"
                + "-fx-border-color: #668DEB; -fx-border-width: 1;"
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        notificationHeader.setStyle("-fx-text-fill: #B7CCFF; -fx-font-size: 11px;"
                + "-fx-font-weight: bold;");
        notificationSymbol.setStyle("-fx-text-fill: #A4BCFF; -fx-font-size: 24px;");
        notificationTitle.setStyle("-fx-text-fill: #C5D5FF; -fx-font-size: 16px;"
                + "-fx-font-weight: bold;");
        notificationDescription.setStyle("-fx-text-fill: #E2EBFF; -fx-font-size: 12px;");
        playNotification();
    }

    private void playNotification() {
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