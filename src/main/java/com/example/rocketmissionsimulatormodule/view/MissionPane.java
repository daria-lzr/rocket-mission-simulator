package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.util.Mission;
import com.example.rocketmissionsimulatormodule.util.MissionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/** Compact mission status and primary controls; event choices live in MissionLogPane. */
public class MissionPane extends VBox {

    private static final String FONT = "'Avenir Next', 'Segoe UI', Arial, sans-serif";

    private final Mission mission;
    private final Label statusValue;
    private final Label activeEventValue;
    private final Button launchButton;
    private final Button deployButton;
    private Runnable onLaunch;
    private Runnable onDeploy;

    public MissionPane(Mission mission) {
        this.mission = mission;
        setPadding(new Insets(16));
        setSpacing(13);
        setStyle("-fx-background-color: #050807;");

        // Match TelemetryPane: 16 px title, 11 px captions, 12 px values.
        Label missionTitle = new Label("MISSION / EVENTS");
        styleLabel(missionTitle, "#FFFFFF", 16, true);

        statusValue = createValue("", "#E7EDF2");
        activeEventValue = createValue("None", "#9AA8B7");
        Label objectiveValue = createValue("Deploy satellite", "#F4F8FA");

        HBox statusRow = createInfoRow("STATUS", statusValue);
        HBox eventRow = createInfoRow("ACTIVE EVENT", activeEventValue);
        HBox objectiveRow = createInfoRow("OBJECTIVE", objectiveValue);

        launchButton = new Button("Launch");
        styleButton(launchButton, "#1C745B");
        launchButton.setPrefHeight(45);
        launchButton.setMinHeight(40);
        launchButton.setOnAction(event -> {
            mission.launch();
            updateMissionStatus();
            launchButton.setDisable(true);
            if (onLaunch != null) {
                onLaunch.run();
            }
        });

        deployButton = new Button("Deploy Satellite");
        styleButton(deployButton, "#225B87");
        deployButton.setPrefHeight(45);
        deployButton.setMinHeight(40);
        deployButton.setDisable(true);
        deployButton.setOnAction(event -> {
            if (onDeploy != null) {
                onDeploy.run();
            }
        });

        getChildren().addAll(missionTitle, statusRow, eventRow,
                objectiveRow, launchButton, deployButton);
        updateMissionStatus();
    }

    private static HBox createInfoRow(String captionText, Label value) {
        Label caption = new Label(captionText);
        styleLabel(caption, "#9AA8B7", 11, true);
        caption.setMinWidth(Region.USE_PREF_SIZE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        value.setTextAlignment(TextAlignment.RIGHT);
        value.setAlignment(Pos.CENTER_RIGHT);
        value.setWrapText(true);

        HBox row = new HBox(8, caption, spacer, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static Label createValue(String text, String color) {
        Label value = new Label(text);
        styleLabel(value, color, 12, false);
        return value;
    }

    private static void styleLabel(Label label, String color, int size, boolean bold) {
        label.setStyle("-fx-font-family: " + FONT + ";"
                + "-fx-font-size: " + size + "px;"
                + (bold ? "-fx-font-weight: bold;" : "")
                + "-fx-text-fill: " + color + ";");
    }

    private static void styleButton(Button button, String color) {
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 5;"
                + "-fx-text-fill: white; -fx-font-weight: bold;"
                + "-fx-font-family: " + FONT + ";"
                + "-fx-font-size: 13px;");
    }

    public void setOnLaunch(Runnable onLaunch) {
        this.onLaunch = onLaunch;
    }

    public void setOnDeploy(Runnable onDeploy) {
        this.onDeploy = onDeploy;
    }

    public void updateMissionStatus() {
        Mission.MissionState state = mission.getCurrentState();
        // Symbols and friendly labels are presentation only; the enum remains unchanged.
        switch (state) {
            case PRE_LAUNCH -> setStatus("○  PRE-LAUNCH", "#CBD5E1");
            case ASCENT -> setStatus("↑  ASCENDING", "#63C7FF");
            case ORBIT -> setStatus("◎  IN ORBIT", "#A5B4FC");
            case DEPLOYMENT -> setStatus("↗  DEPLOYING", "#F5C86A");
            case SUCCESS -> setStatus("✓  SUCCESS", "#86EFAC");
            case FAILURE -> setStatus("✕  FAILURE", "#FF7474");
        }
        deployButton.setDisable(state != Mission.MissionState.ORBIT);
    }

    private void setStatus(String text, String color) {
        statusValue.setText(text);
        styleLabel(statusValue, color, 12, true);
    }

    public void displayMissionEvent(MissionEvent event) {
        switch (event) {
            case ENGINE_OVERHEATING -> {
                activeEventValue.setText("ENGINE OVERHEATING");
                styleLabel(activeEventValue, "#F2C47B", 12, true);
            }
        }
    }

    public void clearMissionEvent() {
        activeEventValue.setText("None");
        styleLabel(activeEventValue, "#9AA8B7", 12, false);
    }
}
