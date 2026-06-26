package simulation;

import java.util.Random;

/**
 * Weather conditions that affect the simulation.
 */
public enum Weather
{
    RAIN("rain"),
    FLOOD("flood"),
    DROUGHT("drought"),
    FOG("fog"),
    NONE("none");

    private final String displayName;

    Weather(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Pick a random weather condition.
     * @param rand The source of randomness.
     * @return A randomly selected weather condition.
     */
    public static Weather randomWeather(Random rand) {
        Weather[] values = values();
        return values[rand.nextInt(values.length)];
    }

    @Override
    public String toString() {
        return displayName;
    }
}
