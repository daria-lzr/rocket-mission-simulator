package com.example.rocketmissionsimulatormodule;

public class Mission {

    public enum MissionStates {
        PRE_LAUNCH("Pre-launch"),
        ASCENT("Ascent"),
        ORBIT("Orbit"),
        DEPLOYMENT("Deployment"),
        SUCCESS("Success"),
        FAILURE("Failure");

        private final String stateName;

        MissionStates(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }
    }

    private MissionStates state;

    public Mission() {
        this.state = MissionStates.PRE_LAUNCH;
    }

    public void updateState(MissionStates state) {
        this.state = state;
    }

    public MissionStates getState() {
        return state;
    }
}
