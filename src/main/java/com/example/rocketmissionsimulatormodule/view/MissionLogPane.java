package com.example.rocketmissionsimulatormodule.view;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
    private final VBox responseCountdown = new VBox(5);
    private final StackPane countdownTrack = new StackPane();
    private final Region countdownFill = new Region();
    private final Text countdownLabel = new Text();
    private final DoubleProperty countdownFraction = new SimpleDoubleProperty(1.0);
    private AnimationTimer countdownAnimation;
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
        initializeCountdown();
        content.getChildren().addAll(messages, responseCountdown, activity);
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
                    if (ascending && !responseCountdown.isVisible()
                            && !initializing && !displayingMessage && pendingMessages.isEmpty()) {
                        dots = (dots + 1) % 4;
                        activity.setText("> ASCENT IN PROGRESS" + ".".repeat(dots));
                        setVvalue(1.0);
                    }
                })
        );
        activityAnimation.setCycleCount(Timeline.INDEFINITE);
        activityAnimation.play();
    }

    private void initializeCountdown() {
        countdownLabel.setFont(font);
        countdownLabel.setFill(Color.web("#FFCE72"));
        countdownLabel.setText("> RESPONSE TIME: 10.0s");

        countdownTrack.setPrefSize(440, 24);
        countdownTrack.setMaxWidth(440);
        countdownTrack.setMinHeight(24);
        countdownTrack.setMaxHeight(24);
        countdownTrack.setAlignment(Pos.CENTER_LEFT);
        countdownTrack.setStyle("-fx-background-color: #152024;"
                + "-fx-border-color: #899AA0; -fx-border-width: 2;"
                + "-fx-padding: 4;");

        countdownFill.setMinWidth(0);
        countdownFill.setMaxWidth(Region.USE_PREF_SIZE);
        countdownFill.setMinHeight(12);
        countdownFill.setPrefHeight(12);
        countdownFill.setMaxHeight(12);
        countdownFill.setStyle("-fx-background-color: #6BE6A5;");
        countdownFill.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(0, (countdownTrack.getWidth() - 12) * countdownFraction.get()),
                countdownTrack.widthProperty(), countdownFraction));
        countdownTrack.getChildren().add(countdownFill);
        StackPane.setAlignment(countdownFill, Pos.CENTER_LEFT);

        responseCountdown.getChildren().addAll(countdownLabel, countdownTrack);
        VBox.setMargin(responseCountdown, new Insets(6, 0, 8, 0));
        responseCountdown.setVisible(false);
        responseCountdown.setManaged(false);
    }

    /** Display a countdown inside the mission log, using the controller's actual deadline. */
    public void showResponseCountdown(long deadlineNanos, long durationNanos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showResponseCountdown(deadlineNanos, durationNanos));
            return;
        }
        hideResponseCountdown();
        responseCountdown.setManaged(true);
        responseCountdown.setVisible(true);
        countdownFraction.set(1.0);
        activity.setText("");

        countdownAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long remaining = Math.max(0, deadlineNanos - System.nanoTime());
                double fraction = durationNanos == 0 ? 0
                        : Math.min(1.0, (double) remaining / durationNanos);
                countdownFraction.set(fraction);
                countdownLabel.setText(String.format(java.util.Locale.US,
                        "> RESPONSE TIME: %.1fs", remaining / 1_000_000_000.0));
                String color = fraction > 0.5 ? "#6BE6A5"
                        : fraction > 0.2 ? "#F5C86A" : "#FF7474";
                countdownFill.setStyle("-fx-background-color: " + color + ";");
                setVvalue(1.0);
                if (remaining == 0) {
                    stop();
                }
            }
        };
        countdownAnimation.start();
    }

    public void hideResponseCountdown() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::hideResponseCountdown);
            return;
        }
        if (countdownAnimation != null) {
            countdownAnimation.stop();
            countdownAnimation = null;
        }
        responseCountdown.setVisible(false);
        responseCountdown.setManaged(false);
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
