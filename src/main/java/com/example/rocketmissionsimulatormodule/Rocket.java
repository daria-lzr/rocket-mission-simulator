package com.example.rocketmissionsimulatormodule;

public class Rocket {

    private double altitude;
    private double velocity;


    private static final double ACCELERATION = 20.0;

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
        velocity += ACCELERATION * deltaTime;
        altitude += velocity * deltaTime;
    }
}