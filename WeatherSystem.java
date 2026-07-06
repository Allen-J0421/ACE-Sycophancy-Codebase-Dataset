import java.util.Random;

/**
 * Manages the current weather state and transitions it each day.
 * Owned by the Simulator; accessed by actors via Simulator.getWeather().
 */
public class WeatherSystem
{
    private final Random rand = Randomizer.getRandom();
    private Weather current;

    /**
     * Create a WeatherSystem with an initial weather state already set.
     */
    public WeatherSystem()
    {
        advance();
    }

    /**
     * Randomly select a new weather state for the next day.
     */
    public void advance()
    {
        current = (rand.nextInt(2) == 1) ? new RainyWeather() : new DryWeather();
    }

    /**
     * @return The current weather state.
     */
    public Weather getCurrent() { return current; }
}
