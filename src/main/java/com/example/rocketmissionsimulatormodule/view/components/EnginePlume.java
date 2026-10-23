package com.example.rocketmissionsimulatormodule.view.components;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.Objects;

public class EnginePlume extends Canvas {

    public static final double WIDTH = 170.0;
    public static final double HEIGHT = 230.0;

    private static final double CENTER_X = WIDTH / 2.0;
    private static final double[] NOZZLE_OFFSETS = {-10.0, -3.5, 3.5, 10.0};
    private static final double[] IGNITION_DELAYS = {0.09, 0.15, 0.22, 0.30};
    private static final long IGNITION_DURATION_NANOS = 3_000_000_000L;

    private final GraphicsContext graphics = getGraphicsContext2D();
    private final AnimationTimer animation = new AnimationTimer() {
        @Override
        public void handle(long now) {
            renderFrame(now);
        }
    };

    private double requestedThrottle;
    private double visibleThrottle;
    private double altitude;
    private long lastFrameNanos;
    private long ignitionStartNanos;
    private boolean igniting;
    private Runnable onIgnitionFinished;

    public EnginePlume() {
        super(WIDTH, HEIGHT);
        setManaged(false);
        setMouseTransparent(true);
    }

    public void ignite(Runnable onFinished) {
        if (igniting) {
            return;
        }
        onIgnitionFinished = Objects.requireNonNull(onFinished);
        igniting = true;
        ignitionStartNanos = 0L;
        lastFrameNanos = 0L;
        requestedThrottle = 1.0;
        visibleThrottle = 0.0;
        animation.start();
    }

    public void setThrottle(double throttle, double altitudeMeters) {
        requestedThrottle = Math.clamp(throttle, 0.0, 1.0);
        altitude = Math.max(0.0, altitudeMeters);
        if (!igniting && (requestedThrottle > 0.001 || visibleThrottle > 0.001)) {
            animation.start();
        }
    }

    public void stopEngines() {
        igniting = false;
        onIgnitionFinished = null;
        requestedThrottle = 0.0;
        if (visibleThrottle > 0.001) {
            animation.start();
        }
    }

    public void dispose() {
        animation.stop();
        igniting = false;
        onIgnitionFinished = null;
        requestedThrottle = 0.0;
        visibleThrottle = 0.0;
        graphics.clearRect(0, 0, WIDTH, HEIGHT);
    }

    private void renderFrame(long now) {
        double elapsedSeconds = lastFrameNanos == 0L ? 1.0 / 60.0 : Math.min(0.1, (now - lastFrameNanos) / 1_000_000_000.0);
        lastFrameNanos = now;

        if (igniting) {
            if (ignitionStartNanos == 0L) {
                ignitionStartNanos = now;
            }
            double progress = Math.min(1.0,
                    (now - ignitionStartNanos) / (double) IGNITION_DURATION_NANOS);
            visibleThrottle = smoothstep(Math.clamp((progress - 0.10) / 0.76, 0.0, 1.0));
            if (progress >= 1.0) {
                igniting = false;
                Runnable callback = onIgnitionFinished;
                onIgnitionFinished = null;
                if (callback != null) {
                    callback.run();
                }
            }
        } else {
            visibleThrottle += (requestedThrottle - visibleThrottle)
                    * Math.min(1.0, elapsedSeconds * 10.5);
        }

        graphics.clearRect(0, 0, WIDTH, HEIGHT);
        if (visibleThrottle < 0.003) {
            if (!igniting && requestedThrottle < 0.003) {
                visibleThrottle = 0.0;
                animation.stop();
            }
            return;
        }

        drawExhaust(now / 1_000_000_000.0);
    }

    private void drawExhaust(double time) {
        double power = Math.clamp(visibleThrottle, 0.0, 1.0);

        double ascentStretch = 0.34 + 0.66 * Math.clamp((altitude - 22.0) / 110.0, 0.0, 1.0);
        double altitudeExpansion = 1.0 + 0.22 * Math.clamp(altitude / 120_000.0, 0.0, 1.0);


        double hazeAlpha = 0.035 * power * ascentStretch;
        graphics.setFill(new LinearGradient(0, 0, 0, 90, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(160, 205, 255, hazeAlpha)),
                new Stop(0.55, Color.rgb(105, 140, 240, hazeAlpha * 0.55)),
                new Stop(1.0, Color.TRANSPARENT)));
        drawMergedHaze((12 + 34 * power) * ascentStretch, 18 * altitudeExpansion);

        for (int i = 0; i < NOZZLE_OFFSETS.length; i++) {
            double enginePower = enginePowerForIndex(i, power, time);
            if (enginePower < 0.02) {
                continue;
            }
            drawSingleEngine(CENTER_X + NOZZLE_OFFSETS[i], enginePower, altitudeExpansion, ascentStretch, time, i);
        }

        double padFactor = 1.0 - Math.clamp(altitude / 180.0, 0.0, 1.0);
        if (padFactor > 0.0) {
            double glowRadiusX = 18.0 + 6.0 * power;
            double glowRadiusY = 5.0 + 2.0 * power;
            graphics.setFill(new RadialGradient(0, 0, CENTER_X, 42.0,
                    glowRadiusX, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(255, 215, 160, 0.10 * power * padFactor)),
                    new Stop(1.0, Color.TRANSPARENT)));
            graphics.fillOval(CENTER_X - glowRadiusX, 42.0 - glowRadiusY,
                    glowRadiusX * 2.0, glowRadiusY * 2.0);
        }
    }

    private void drawSingleEngine(double nozzleCenterX, double enginePower, double altitudeExpansion, double ascentStretch, double time, int engineIndex) {
        double flicker = 1.0
                + 0.05 * Math.sin(time * (28.0 + engineIndex * 1.5) + engineIndex)
                + 0.025 * Math.sin(time * (44.0 + engineIndex) + 0.9 * engineIndex);

        double plumeLength = Math.min(HEIGHT - 18.0, (13.0 + 46.0 * Math.pow(enginePower, 0.86)) * ascentStretch * flicker);
        double outerBottomRadius = (2.5 + 2.6 * enginePower) * altitudeExpansion;
        double innerBottomRadius = (1.15 + 1.55 * enginePower) * altitudeExpansion;
        double topOuterRadius = 1.8 + 0.45 * enginePower;
        double topInnerRadius = 0.75 + 0.28 * enginePower;

        graphics.setFill(new LinearGradient(0, 0, 0, plumeLength, false,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 255, 255, 0.78 * enginePower)),
                new Stop(0.15, Color.rgb(215, 244, 255, 0.67 * enginePower)),
                new Stop(0.58, Color.rgb(125, 188, 255, 0.33 * enginePower)),
                new Stop(0.88, Color.rgb(116, 110, 225, 0.13 * enginePower)),
                new Stop(1.0, Color.TRANSPARENT)));
        drawPlumeShape(nozzleCenterX, plumeLength, topOuterRadius, outerBottomRadius,
                time, 0.45 + engineIndex * 0.33);

        graphics.setFill(new LinearGradient(0, 0, 0, plumeLength * 0.72, false,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 255, 255, 0.92 * enginePower)),
                new Stop(0.18, Color.rgb(235, 249, 255, 0.80 * enginePower)),
                new Stop(0.55, Color.rgb(190, 230, 255, 0.34 * enginePower)),
                new Stop(1.0, Color.TRANSPARENT)));
        drawPlumeShape(nozzleCenterX, plumeLength * 0.72, topInnerRadius, innerBottomRadius,
                time, 1.2 + engineIndex * 0.41);

        drawShockDiamonds(nozzleCenterX, plumeLength, innerBottomRadius, enginePower);
        drawNozzleGlow(nozzleCenterX, enginePower);
    }

    private void drawNozzleGlow(double centerX, double enginePower) {
        double glowRadius = 3.4 + 2.1 * enginePower;
        graphics.setFill(new RadialGradient(0, 0, centerX, 9.5,
                glowRadius, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.rgb(255, 255, 255, 0.95 * enginePower)),
                new Stop(0.36, Color.rgb(210, 240, 255, 0.65 * enginePower)),
                new Stop(1.0, Color.TRANSPARENT)));
        graphics.fillOval(centerX - glowRadius, 9.5 - glowRadius,
                glowRadius * 2.0, glowRadius * 2.0);
    }

    private void drawShockDiamonds(double centerX, double plumeLength, double innerBottomRadius, double enginePower) {
        if (enginePower < 0.24) {
            return;
        }

        double[] ys = {15.0, 22.0, 30.0};
        double[] widths = {
                1.8 + innerBottomRadius * 0.52,
                1.55 + innerBottomRadius * 0.44,
                1.25 + innerBottomRadius * 0.34
        };

        for (int i = 0; i < ys.length; i++) {
            if (ys[i] >= plumeLength * 0.78) {
                continue;
            }
            double halfWidth = widths[i];
            double halfHeight = 3.0 - i * 0.45;
            double[] x = {centerX, centerX - halfWidth, centerX, centerX + halfWidth};
            double[] y = {ys[i] - halfHeight, ys[i], ys[i] + halfHeight, ys[i]};
            graphics.setFill(Color.rgb(255, 255, 255, (0.18 - i * 0.03) * enginePower));
            graphics.fillPolygon(x, y, 4);
        }
    }

    private void drawMergedHaze(double length, double bottomHalfWidth) {
        int segments = 12;
        int count = (segments + 1) * 2;
        double[] x = new double[count];
        double[] y = new double[count];

        for (int i = 0; i <= segments; i++) {
            double progress = i / (double) segments;
            double halfWidth = 5.5 + (bottomHalfWidth - 5.5) * Math.pow(progress, 0.9);
            double vertical = 14.0 + progress * length;
            x[i] = CENTER_X - halfWidth;
            y[i] = vertical;
            x[count - 1 - i] = CENTER_X + halfWidth;
            y[count - 1 - i] = vertical;
        }
        graphics.fillPolygon(x, y, count);
    }

    private void drawPlumeShape(double center, double length, double topRadius, double bottomRadius, double time, double phase) {
        int segments = 16;
        int count = (segments + 1) * 2;
        double[] x = new double[count];
        double[] y = new double[count];

        for (int i = 0; i <= segments; i++) {
            double progress = i / (double) segments;
            double halfWidth = topRadius + (bottomRadius - topRadius) * Math.pow(progress, 0.84);
            double sway = progress * (Math.sin(time * 15.0 + phase + progress * 8.0) * 0.95 + Math.sin(time * 23.0 + phase * 1.4 + progress * 11.0) * 0.45);
            double vertical = 10.0 + progress * length;
            x[i] = center + sway - halfWidth;
            y[i] = vertical;
            x[count - 1 - i] = center + sway + halfWidth;
            y[count - 1 - i] = vertical;
        }

        graphics.fillPolygon(x, y, count);
    }

    private double enginePowerForIndex(int index, double globalPower, double time) {
        if (igniting && ignitionStartNanos != 0L) {
            double ignitionProgress = (lastFrameNanos - ignitionStartNanos) / (double) IGNITION_DURATION_NANOS;
            double stagedProgress = Math.clamp((ignitionProgress - IGNITION_DELAYS[index]) / 0.42, 0.0, 1.0);
            return globalPower * smoothstep(stagedProgress);
        }

        double variation = 1.0 + 0.03 * Math.sin(time * (6.0 + index * 0.4) + index * 0.8);
        return Math.clamp(globalPower * variation, 0.0, 1.0);
    }

    private static double smoothstep(double fraction) {
        return fraction * fraction * (3.0 - 2.0 * fraction);
    }
}
