import java.util.Random;

/**
 * Manages the weather state for the simulation.
 *
 * <p>Encapsulates the current weather condition and the rules for
 * transitioning to a new one each step. Separating this from the
 * simulation loop means new weather patterns (additional conditions,
 * seasonal cycles, weighted distributions, etc.) can be introduced
 * here without touching {@link Simulator}.
 *
 * <p>Weather is advanced by calling {@link #update()} once per
 * simulation step. The current condition is retrieved via
 * {@link #getCurrent()}.
 */
public class WeatherManager
{
    // Approximate probability for each of the first two conditions;
    // anything that doesn't match falls through to "Sunny".
    private static final double FOGGY_PROBABILITY = 0.33;
    private static final double RAINY_PROBABILITY = 0.33;

    private static final Random rand = Randomizer.getRandom();

    // The current weather condition.
    private String current;

    /**
     * Create a WeatherManager. The initial condition is set by calling
     * {@link #update()} so the first display is always randomly chosen.
     */
    public WeatherManager()
    {
        update();
    }

    /**
     * Advance to a new randomly-chosen weather condition.
     * Called once at the start of each simulation step.
     */
    public void update()
    {
        if (rand.nextDouble() <= FOGGY_PROBABILITY) {
            current = "Foggy";
        } else if (rand.nextDouble() <= RAINY_PROBABILITY) {
            current = "Rainy";
        } else {
            current = "Sunny";
        }
    }

    /**
     * @return the current weather condition.
     */
    public String getCurrent()
    {
        return current;
    }
}
