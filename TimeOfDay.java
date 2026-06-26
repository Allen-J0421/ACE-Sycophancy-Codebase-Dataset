/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An enumeration for the time-of-day in the simulation.
 *
 * @version 2022.03.02
 */
public enum TimeOfDay {
    EARLY_MORNING,
    SUNRISE,
    LATE_MORNING,
    MIDDAY,
    EARLY_AFTERNOON,
    LATE_AFTERNOON,
    SUNSET,
    EVENING,
    NIGHT,
    AROUND_MIDNIGHT;

    // define fields
    private static final TimeOfDay[] values = values();

    /**
     * Gets the next time-of-day value in succession.
     *
     * @return A TimeOfDay constant.
     */
    public TimeOfDay next() {
        return values[(this.ordinal()+1) % values.length];
    }
}
