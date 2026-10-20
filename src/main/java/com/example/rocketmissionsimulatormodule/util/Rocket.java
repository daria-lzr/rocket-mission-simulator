package com.example.rocketmissionsimulatormodule.util;

public class Rocket {

    private double altitude;
    private double velocity;


    private static final double DEFAULT_ACCELERATION = 2.0;

    private volatile double currentAcceleration = DEFAULT_ACCELERATION;

    public Rocket() {
        this.altitude = 0.0;
        this.velocity = 0.0;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getVelocity() {
        return velocity;
    }


    public void updateFlight(double deltaTime) {
        velocity += currentAcceleration * deltaTime;
        altitude += velocity * deltaTime;
    }

    public void reduceAcceleration() {
        currentAcceleration = DEFAULT_ACCELERATION / 2.0;
    }

    public void restoreAcceleration() {
        currentAcceleration = DEFAULT_ACCELERATION;
    }
}