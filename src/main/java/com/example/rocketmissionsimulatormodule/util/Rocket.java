package com.example.rocketmissionsimulatormodule.util;

/**
 * Simplified vertical flight model. Uses SI units and a guided suborbital ascent.
 * This is not an orbital mechanics model or a calibrated Starship simulation.
 */
public class Rocket {
    private static final double EARTH_RADIUS_M = 6_371_000.0;
    private static final double G0 = 9.80665;
    private static final double GRAVITATIONAL_PARAMETER = G0 * EARTH_RADIUS_M * EARTH_RADIUS_M;
    private static final double SEA_LEVEL_DENSITY = 1.225;
    private static final double ATMOSPHERE_SCALE_HEIGHT_M = 8_500.0;
    private static final double DRAG_COEFFICIENT = 0.30;
    private static final double CROSS_SECTION_M2 = 63.6;

    // Illustrative single-stage vehicle, not measured Starship values.
    private static final double DRY_MASS_KG = 150_000.0;
    private static final double INITIAL_PROPELLANT_KG = 350_000.0;
    private static final double SEA_LEVEL_THRUST_N = 10_000_000.0;
    private static final double SEA_LEVEL_ISP_S = 330.0;
    private static final double VACUUM_ISP_S = 370.0;
    private static final double FULL_THROTTLE_MASS_FLOW_KG_S =
            SEA_LEVEL_THRUST_N / (SEA_LEVEL_ISP_S * G0);

    private static final double MAX_DYNAMIC_PRESSURE_PA = 25_000.0;
    private static final double MAX_NET_ACCELERATION_M_S2 = 3.0 * G0;
    private static final double GUIDANCE_START_ALTITUDE_M = 40_000.0;
    private static final double APOGEE_MARGIN_M = 2_000.0;

    private final double targetApogeeMeters;
    private double altitude;
    private double velocity;
    private double acceleration;
    private double remainingPropellant = INITIAL_PROPELLANT_KG;
    private double throttle = 1.0;
    private double throttleLimit = 1.0;
    private double recoveryThrottleFactor = 1.0;
    private boolean engineCutoff;
    private boolean hasLeftGround;
    private boolean crashed;

    public Rocket() {
        this(300_000.0);
    }

    public Rocket(double targetAltitudeMeters) {
        if (!Double.isFinite(targetAltitudeMeters) || targetAltitudeMeters <= 0) {
            throw new IllegalArgumentException("Target altitude must be positive and finite");
        }
        targetApogeeMeters = targetAltitudeMeters + APOGEE_MARGIN_M;
    }

    /** Advance the model using a positive interval in simulated seconds. */
    public synchronized void updateFlight(double deltaTime) {
        if (!(deltaTime > 0.0) || !Double.isFinite(deltaTime)) {
            throw new IllegalArgumentException("deltaTime must be positive and finite");
        }
        if (crashed) {
            return;
        }

        double density = SEA_LEVEL_DENSITY * Math.exp(-altitude / ATMOSPHERE_SCALE_HEIGHT_M);
        double gravity = G0 * Math.pow(EARTH_RADIUS_M / (EARTH_RADIUS_M + altitude), 2);
        double isp = VACUUM_ISP_S
                - (VACUUM_ISP_S - SEA_LEVEL_ISP_S) * density / SEA_LEVEL_DENSITY;
        double dynamicPressure = 0.5 * density * velocity * velocity;
        double mass = DRY_MASS_KG + remainingPropellant;
        double fullThrust = FULL_THROTTLE_MASS_FLOW_KG_S * isp * G0;
        double drag = 0.5 * density * DRAG_COEFFICIENT
                * CROSS_SECTION_M2 * velocity * Math.abs(velocity);

        // Throttle back around max-Q, and avoid excessive net acceleration.
        double pressureThrottle = dynamicPressure > MAX_DYNAMIC_PRESSURE_PA
                ? Math.max(0.62, Math.sqrt(MAX_DYNAMIC_PRESSURE_PA / dynamicPressure))
                : 1.0;
        double accelerationThrottle = (mass * (MAX_NET_ACCELERATION_M_S2 + gravity) + drag)
                / fullThrust;
        double automaticThrottle = Math.min(1.0,
                Math.min(pressureThrottle, Math.max(0.0, accelerationThrottle)));

        // Once above the dense atmosphere, predict the unpowered apogee from
        // radial mechanical energy. Cut off early enough to coast to target.
        if (!engineCutoff && altitude >= GUIDANCE_START_ALTITUDE_M && velocity > 0.0
                && estimateUnpoweredApogee() >= targetApogeeMeters) {
            engineCutoff = true;
        }

        throttle = engineCutoff || remainingPropellant <= 0.0 ? 0.0
                : Math.min(automaticThrottle, throttleLimit) * recoveryThrottleFactor;

        // The mass flow rate determines both consumed propellant and thrust.
        double requestedBurn = FULL_THROTTLE_MASS_FLOW_KG_S * throttle * deltaTime;
        double burned = Math.min(remainingPropellant, requestedBurn);
        double thrust = (burned / deltaTime) * isp * G0;
        double averageMass = mass - burned / 2.0;
        acceleration = (thrust - drag) / averageMass - gravity;
        double nextVelocity = velocity + acceleration * deltaTime;
        double nextAltitude = altitude + 0.5 * (velocity + nextVelocity) * deltaTime;
        remainingPropellant -= burned;

        if (nextAltitude > 0.0) {
            altitude = nextAltitude;
            velocity = nextVelocity;
            hasLeftGround = true;
        } else {
            crashed = hasLeftGround;
            altitude = 0.0;
            velocity = 0.0;
            acceleration = 0.0;
        }
    }

    private double estimateUnpoweredApogee() {
        double radius = EARTH_RADIUS_M + altitude;
        double reciprocalApogee = 1.0 / radius
                - velocity * velocity / (2.0 * GRAVITATIONAL_PARAMETER);
        return reciprocalApogee <= 0.0 ? Double.POSITIVE_INFINITY
                : 1.0 / reciprocalApogee - EARTH_RADIUS_M;
    }

    public synchronized double getAltitude() { return altitude; }
    /** Signed vertical velocity: positive for upward flight (m/s). */
    public synchronized double getVelocity() { return velocity; }
    public synchronized double getAcceleration() { return acceleration; }
    public synchronized double getFuelFraction() {
        return remainingPropellant / INITIAL_PROPELLANT_KG;
    }
    public synchronized double getFuelKilograms() { return remainingPropellant; }
    public synchronized double getThrottle() { return throttle; }
    public synchronized double getEffectiveThrottle() { return throttle; }
    public synchronized double getMassKilograms() {
        return DRY_MASS_KG + remainingPropellant;
    }
    public synchronized boolean isOutOfFuel() { return remainingPropellant <= 0.0; }
    public synchronized boolean hasCrashed() { return crashed; }
    public synchronized boolean hasLeftGround() { return hasLeftGround; }
    public synchronized boolean hasEngineCutoff() { return engineCutoff; }
    public synchronized boolean isCoasting() {
        return throttle == 0.0 && hasLeftGround;
    }
    public synchronized double getDynamicPressure() {
        double density = SEA_LEVEL_DENSITY * Math.exp(-altitude / ATMOSPHERE_SCALE_HEIGHT_M);
        return 0.5 * density * velocity * velocity;
    }

    /** Optional player cap on automatic throttle (0 = engines off, 1 = no cap). */
    public synchronized void setThrottle(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Throttle must be finite");
        }
        throttleLimit = Math.max(0.0, Math.min(1.0, value));
    }

    /** Existing overheating response now limits thrust for its entire duration. */
    public synchronized void reduceAcceleration() {
        recoveryThrottleFactor = 0.5;
    }

    public synchronized void restoreAcceleration() {
        recoveryThrottleFactor = 1.0;
    }
}
