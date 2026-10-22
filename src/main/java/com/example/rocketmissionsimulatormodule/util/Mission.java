package com.example.rocketmissionsimulatormodule.util;


public class Mission {

    private final Rocket rocket;

    public enum MissionState {
        PRE_LAUNCH,
        ASCENT,
        ORBIT,
        DEPLOYMENT,
        SUCCESS,
        FAILURE
    }


    private volatile MissionState currentState;

    public static final double TARGET_ALTITUDE = 300000.0;
    public static final double VELOCITY_DISPLAY_SCALE = 2500.0;


    public Mission() {
        this.currentState = MissionState.PRE_LAUNCH;
        this.rocket = new Rocket(TARGET_ALTITUDE);
    }

    public MissionState getCurrentState() {
        return currentState;
    }


    public void launch(){
        if(currentState == MissionState.PRE_LAUNCH){
            currentState = MissionState.ASCENT;
        }

    }

    public Rocket getRocket() {
        return rocket;
    }


    public boolean checkOrbitReached() {
        if (currentState != MissionState.ASCENT) {
            return false;
        }

        // The mission pauses at apogee, not at the first crossing of 300 km.
        // Vertical velocity should be approximately zero at this point.
        // This is still a deployment-altitude milestone, not a real orbit.
        if (rocket.hasLeftGround() && rocket.getAltitude() >= TARGET_ALTITUDE
                && rocket.getVelocity() <= 0.0) {
            currentState = MissionState.ORBIT;
            return true;
        }

        return false;
    }


    public void deploySatellite() {
        if (currentState == MissionState.ORBIT) {
            currentState = MissionState.DEPLOYMENT;
        }
    }

    public void completeMission() {
        if (currentState == MissionState.DEPLOYMENT) {
            currentState = MissionState.SUCCESS;
        }
    }

    public void failMission() {
        if (currentState != MissionState.SUCCESS && currentState != MissionState.FAILURE) {
            currentState = MissionState.FAILURE;
        }
    }


}
