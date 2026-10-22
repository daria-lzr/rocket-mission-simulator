package com.example.rocketmissionsimulatormodule.util;

import javafx.beans.binding.DoubleExpression;

public class Rocket {
    private double altitude;
    private double velocity;

    private volatile double throttle = 1.0;
    private volatile double enginePowerMultiplier = 1.0;
    private volatile double fuelPercent = 100.0;

    private static final double MAX_ACCELERATION = 2.0;

    //Fuel consumption in percentage points per second
    //at 100% effective throttle.
    private static final double FUEL_CONSUMPTION_RATE = 0.5;

    public Rocket(){
        altitude = 0.0;
        velocity = 0.0;
    }

    public double getAltitude() { return altitude; }

    public double getVelocity() { return velocity; }

    public double getFuelPercent() { return fuelPercent; }

    public double getEffectiveThrottle() {
        if(fuelPercent <= 0.0){
            return 0.0;
        }

        return throttle * enginePowerMultiplier;
    }

    public void setThrottle(double throttle){
        if(!Double.isFinite(throttle)) { return; }
        this.throttle = Math.max(0.0, Math.min(1.0, throttle));
    }

    public void updateFlight(double deltaTime){
        if(!Double.isFinite(deltaTime) || deltaTime <= 0.0) { return; }

        double effectiveThrottle = getEffectiveThrottle();

        velocity += MAX_ACCELERATION * effectiveThrottle * deltaTime;
        altitude += velocity * deltaTime;

        fuelPercent = Math.max(
                0.0,
                fuelPercent - FUEL_CONSUMPTION_RATE * effectiveThrottle * deltaTime
        );
    }

    public void reduceAcceleration(){
        enginePowerMultiplier = 0.5;
    }

    public void restoreAcceleration(){
        enginePowerMultiplier = 1.0;
    }

}