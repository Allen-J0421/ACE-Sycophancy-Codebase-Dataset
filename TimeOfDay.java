/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An enumeration for the time-of-day in the simulation.
 *
 * @version 2022.03.02
 */
public enum TimeOfDay {
    EARLY_MORNING,                  // possibly change these to 00:00  to 02:00
    SUNRISE,    //this default reset
    LATE_MORNING,   //goat is 10% less active
    MIDDAY,
    EARLY_AFTERNOON, //cheetah sleeps
    LATE_AFTERNOON,
    SUNSET,  //vulture sleeps, elephant is 15% less active
    EVENING,
    NIGHT,  //lion sleeps
    AROUND_MIDNIGHT; // Zebra is 10% less active

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
