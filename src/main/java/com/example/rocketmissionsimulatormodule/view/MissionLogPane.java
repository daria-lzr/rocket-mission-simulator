package com.example.rocketmissionsimulatormodule.view;

import com.example.rocketmissionsimulatormodule.util.MissionEvent;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
    private static final String BUTTON_STYLE = "-fx-background-color: #111A20;"
            + "-fx-border-color: #34434B; -fx-border-width: 1;"
            + "-fx-text-fill: #F5F5F5; -fx-alignment: CENTER_LEFT;"
            + "-fx-background-radius: 3; -fx-border-radius: 3;";
    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: #24333B;"
            + "-fx-border-color: #8AC6D6; -fx-border-width: 1;"
            + "-fx-text-fill: #FFFFFF; -fx-alignment: CENTER_LEFT;"
            + "-fx-background-radius: 3; -fx-border-radius: 3;";

    private final Queue<String> pendingMessages = new ArrayDeque<>();
    private final TextFlow messages = new TextFlow();
    private final Text activity = new Text();
    private final VBox content = new VBox(5);
    private final VBox eventPanel = new VBox(8);
    private final VBox responseCountdown = new VBox(5);
    private final StackPane countdownTrack = new StackPane();
    private final Region countdownFill = new Region();
    private final Text countdownLabel = new Text();
    private final DoubleProperty countdownFraction = new SimpleDoubleProperty(1.0);
    private final Font font;

    private AnimationTimer countdownAnimation;
    private Timeline messageDelay;
    private Timeline startupAnimation;
    private Timeline activityAnimation;
    private Consumer<Integer> onEventResponse;
    private boolean initializing = true;
    private boolean displayingMessage;
    private boolean ascending;
    private int dots;

    public MissionLogPane() {
        Font loaded = null;
        try (var stream = getClass().getResourceAsStream("/fonts/VT323-Regular.ttf")) {
            if (stream != null) {
                loaded = Font.loadFont(stream, 18);
            }
        } catch (IOException ex) {
            //this font is optional; fall back to a standard monospace font
        }
        font = loaded != null ? loaded : Font.font("Monospaced", 14);

        setEditableAppearance();
        messages.setLineSpacing(3);
        activity.setFont(font);
        activity.setFill(Color.web("#BFC7D1"));
        initializeEventPanel();
        content.getChildren().addAll(messages, eventPanel, activity);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: #050807;");
        setContent(content);

        content.heightProperty().addListener((obs, oldHeight, newHeight) -> setVvalue(1.0));

        addEntry("OBJECTIVE: Reach 300 km and deploy the satellite.");
        addEntry("NEXT STEP: Press Launch to begin.");
        animateStartup();

        activityAnimation = new Timeline(
                new KeyFrame(Duration.millis(400), e -> {
                    if (ascending && !eventPanel.isVisible()
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

    private void setEditableAppearance() {
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setPrefHeight(320);
        setMinHeight(320);
        setMaxHeight(320);
        setFocusTraversable(false);
        setStyle("-fx-background: #050807; -fx-background-color: #050807;"
                + "-fx-border-color: #252B30;");
    }

    private void initializeEventPanel() {
        Text actionLabel = new Text("> SELECT EMERGENCY RESPONSE:");
        actionLabel.setFont(font);
        actionLabel.setFill(Color.web("#F5C86A"));
        eventPanel.getChildren().add(actionLabel);

        countdownLabel.setFont(font);
        countdownLabel.setFill(Color.web("#FFCE72"));
        countdownLabel.setText("> RESPONSE TIME: 20.0s");

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
        VBox.setMargin(responseCountdown, new Insets(3, 0, 4, 0));
        eventPanel.setPadding(new Insets(8, 0, 6, 0));
        eventPanel.setVisible(false);
        eventPanel.setManaged(false);
    }


    public void setOnEventResponse(Consumer<Integer> handler) {
        onEventResponse = handler;
    }

    public void showEventPrompt(MissionEvent event, long deadlineNanos, long durationNanos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showEventPrompt(event, deadlineNanos, durationNanos));
            return;
        }

        hideEventPrompt();
        flushPendingMessages();

        if (event == MissionEvent.ENGINE_OVERHEATING) {
            appendMessage("WARNING: Engine overheating detected!");
            appendMessage("INSTRUCTION: Select a response within 20 seconds.");

            addResponseButton("[1] REDUCE ENGINE THROTTLE", 0);
            addResponseButton("[2] ACTIVATE EMERGENCY COOLING", 1);
            addResponseButton("[3] IGNORE WARNING", 2);
        } else if (event == MissionEvent.AERODYNAMIC_STRESS) {
            appendMessage("WARNING: High aerodynamic stress detected near Max-Q!");
            appendMessage("INSTRUCTION: Select a response within 20 seconds.");

            addResponseButton("[1] INCREASE THRUST TO EXIT DENSE AIR", 0);
            addResponseButton("[2] REDUCE THROTTLE TO LOWER PRESSURE", 1);
            addResponseButton("[3] ACTIVATE ATTITUDE-CONTROL THRUSTERS", 2);
        } else if (event == MissionEvent.PROPELLANT_INSTABILITY) {
            appendMessage("WARNING: Propellant oscillations detected!");
            appendMessage("DIAGNOSTIC: Tank pressure NOMINAL; sloshing increasing.");
            appendMessage("INSTRUCTION: Select a response within 20 seconds.");

            addResponseButton("[1] INCREASE TANK PRESSURIZATION", 0);
            addResponseButton("[2] APPLY RAPID ATTITUDE CORRECTIONS", 1);
            addResponseButton("[3] ACTIVATE PROPELLANT SLOSH DAMPING", 2);
        } else if (event == MissionEvent.ATTITUDE_CONTROL_FAILURE) {
            appendMessage("WARNING: Attitude drift exceeds safe deployment limits!");
            appendMessage("DIAGNOSTIC: Guidance ONLINE; main engine CUTOFF.");
            appendMessage("DIAGNOSTIC: Primary RCS LOW; backup RCS NOMINAL.");
            appendMessage("INSTRUCTION: Restore attitude control within 20 seconds.");

            addResponseButton("[1] RESTART GUIDANCE COMPUTER", 0);
            addResponseButton("[2] REIGNITE MAIN ENGINE", 1);
            addResponseButton("[3] ISOLATE PRIMARY RCS / ENABLE BACKUP", 2);
        }

        eventPanel.getChildren().add(responseCountdown);
        eventPanel.setVisible(true);
        eventPanel.setManaged(true);
        activity.setText("");
        setVvalue(1.0);
        startResponseCountdown(deadlineNanos, durationNanos);
    }

    private void addResponseButton(String label, int index) {
        Button button = new Button(label);
        button.setFont(font);
        button.setMinHeight(34);
        button.setPrefHeight(34);
        button.setMaxWidth(440);
        button.setStyle(BUTTON_STYLE);
        button.setOnMouseEntered(e -> button.setStyle(BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(BUTTON_STYLE));
        button.setOnAction(e -> {
            button.setDisable(true);
            if (onEventResponse != null) {
                onEventResponse.accept(index);
            }
        });
        eventPanel.getChildren().add(button);
    }

    private void startResponseCountdown(long deadlineNanos, long durationNanos) {
        countdownFraction.set(1.0);
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
                if (remaining == 0) {
                    stop();
                }
            }
        };
        countdownAnimation.start();
    }

    //remove controls as soon as a response is chosen or the mission ends
    public void hideEventPrompt() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::hideEventPrompt);
            return;
        }
        if (countdownAnimation != null) {
            countdownAnimation.stop();
            countdownAnimation = null;
        }
        //preserve the first heading
        //rebuild the buttons for the next event
        eventPanel.getChildren().removeIf(node -> node instanceof Button);
        eventPanel.getChildren().remove(responseCountdown);
        eventPanel.setVisible(false);
        eventPanel.setManaged(false);
    }

    private void animateStartup() {
        Text startup = createText("> INITIALIZING MISSION CONTROL", Color.WHITE);
        messages.getChildren().add(startup);
        startupAnimation = new Timeline(
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
        startupAnimation.play();
    }


     //stops all running animations and timers and clears any pending messages
     //used when restarting the mission so that the old mission log doesn't keep running in the background
    public void dispose() {
        setAscending(false);
        hideEventPrompt();
        if (messageDelay != null) {
            messageDelay.stop();
            messageDelay = null;
        }
        if (startupAnimation != null) {
            startupAnimation.stop();
            startupAnimation = null;
        }
        if (activityAnimation != null) {
            activityAnimation.stop();
            activityAnimation = null;
        }
        displayingMessage = false;
        pendingMessages.clear();
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


    public void addImmediateEntry(String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> addImmediateEntry(message));
            return;
        }
        flushPendingMessages();
        appendMessage(message);
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
        messageDelay = new Timeline(new KeyFrame(LINE_DELAY, e -> {
            appendMessage(pendingMessages.remove());
            displayingMessage = false;
            messageDelay = null;
            showNextMessage();
        }));
        messageDelay.play();
    }

    private void flushPendingMessages() {
        if (messageDelay != null) {
            messageDelay.stop();
            messageDelay = null;
        }
        displayingMessage = false;
        while (!pendingMessages.isEmpty()) {
            appendMessage(pendingMessages.remove());
        }
    }

    private void appendMessage(String message) {
        messages.getChildren().add(createText("> " + message + "\n", colorFor(message)));
        setVvalue(1.0);
    }

    private Text createText(String value, Color color) {
        Text text = new Text(value);
        text.setFont(font);
        text.setFill(color);
        return text;
    }

    private Color colorFor(String message) {
        if (message.startsWith("WARNING:") || message.startsWith("FAILURE:")
                || message.startsWith("INCORRECT RESPONSE:") || message.startsWith("TIMEOUT:")) {
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
