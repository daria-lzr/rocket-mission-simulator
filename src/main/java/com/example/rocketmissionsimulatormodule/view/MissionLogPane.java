package com.example.rocketmissionsimulatormodule.view;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class MissionLogPane extends ScrollPane {

    private static final Duration LINE_DELAY = Duration.millis(420);
    private final Queue<String> pendingMessages = new ArrayDeque<>();
    private final TextFlow messages = new TextFlow();
    private final Text activity = new Text();
    private final VBox content = new VBox(5);
    private final Font font;
    private boolean initializing = true;
    private boolean displayingMessage;
    private boolean ascending;
    private int dots;

    public MissionLogPane() {
        Font loaded = null;
        try (var stream = getClass().getResourceAsStream("/fonts/VT323-Regular.ttf")) {
            if (stream != null) {
                loaded = Font.loadFont(stream, 22);
            }
        } catch (IOException ex) {
            // Optional font: use the built-in fallback.
        }
        font = loaded != null ? loaded : Font.font("Monospaced", 16);

        setEditableAppearance();
        messages.setLineSpacing(3);
        activity.setFont(font);
        activity.setFill(Color.web("#BFC7D1"));
        content.getChildren().addAll(messages, activity);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: #050807;");
        setContent(content);

        // Keep the newest entry in view, including when messages wrap.
        content.heightProperty().addListener((obs, oldValue, newValue) -> setVvalue(1.0));

        addEntry("OBJECTIVE: Reach orbit and deploy the satellite.");
        addEntry("NEXT STEP: Press Launch to begin.");
        animateStartup();

        Timeline activityAnimation = new Timeline(
                new KeyFrame(Duration.millis(400), e -> {
                    if (ascending && !initializing && !displayingMessage && pendingMessages.isEmpty()) {
                        dots = (dots + 1) % 4;
                        activity.setText("> ASCENT IN PROGRESS" + ".".repeat(dots));
                        setVvalue(1.0);
                    }
                })
        );
        activityAnimation.setCycleCount(Timeline.INDEFINITE);
        activityAnimation.play();
    }

    private void setEditableAppearance() {
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setPrefHeight(230);
        setMinHeight(190);
        setFocusTraversable(false);
        setStyle("-fx-background: #050807; -fx-background-color: #050807;"
                + "-fx-border-color: #252B30;");
    }

    private void animateStartup() {
        Text startup = createText("> INITIALIZING MISSION CONTROL", Color.WHITE);
        messages.getChildren().add(startup);
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), e -> startup.setText("> INITIALIZING MISSION CONTROL.")),
                new KeyFrame(Duration.millis(600), e -> startup.setText("> INITIALIZING MISSION CONTROL..")),
                new KeyFrame(Duration.millis(900), e -> startup.setText("> INITIALIZING MISSION CONTROL...")),
                new KeyFrame(Duration.millis(1200), e -> {
                    startup.setText("> MISSION CONTROL ONLINE\n");
                    startup.setFill(Color.web("#8EE3A1"));
                    initializing = false;
                    showNextMessage();
                })
        );
        animation.play();
    }

    public void addEntry(String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> addEntry(message));
            return;
        }
        pendingMessages.add(message);
        activity.setText("");
        showNextMessage();
    }

    public void setAscending(boolean value) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setAscending(value));
            return;
        }
        ascending = value;
        dots = 0;
        activity.setText("");
    }

    private void showNextMessage() {
        if (initializing || displayingMessage || pendingMessages.isEmpty()) {
            return;
        }
        displayingMessage = true;
        Timeline delay = new Timeline(new KeyFrame(LINE_DELAY, e -> {
            String message = pendingMessages.remove();
            messages.getChildren().add(createText("> " + message + "\n", colorFor(message)));
            setVvalue(1.0);
            displayingMessage = false;
            showNextMessage();
        }));
        delay.play();
    }

    private Text createText(String value, Color color) {
        Text text = new Text(value);
        text.setFont(font);
        text.setFill(color);
        return text;
    }

    private Color colorFor(String message) {
        if (message.startsWith("WARNING:") || message.startsWith("FAILURE:")) {
            return Color.web("#FF7474");
        }
        if (message.startsWith("SUCCESS:") || message.startsWith("RECOVERY:")
                || message.startsWith("ORBIT:") || message.startsWith("LIFTOFF:")) {
            return Color.web("#8EE3A1");
        }
        if (message.startsWith("INSTRUCTION:") || message.startsWith("NEXT STEP:")) {
            return Color.web("#F5D58A");
        }
        return Color.web("#F5F5F5");
    }
}
