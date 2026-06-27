import java.util.Map;

/**
 * Stores the current environmental state of the simulator.
 *
 * Weather management is delegated to a {@link WeatherSystem} strategy,
 * which can be supplied at construction time.  The default strategy is
 * {@link RandomWeatherSystem}.  All other callers ({@link Simulator},
 * {@link Animal}, {@link Grass}) use the same public API as before.
 *
 * @version 13.7.6
 */
class SimulatorState {

    private double currentNormalisedTime = 0;
    private final WeatherSystem weatherSystem;
    private Map<Class, Counter> counters;

    /** Constructs a state using the default {@link RandomWeatherSystem}. */
    SimulatorState()
    {
        this(new RandomWeatherSystem(Weather.Sunny));
    }

    /**
     * Constructs a state using the supplied weather strategy.
     * @param weatherSystem the strategy that controls weather transitions
     */
    SimulatorState(WeatherSystem weatherSystem)
    {
        this.weatherSystem = weatherSystem;
    }

    /**
     * Set the normalised time (0–1) for this step.
     * @param time the normalised time
     */
    public void setNormalisedTime(double time)
    {
        currentNormalisedTime = time;
    }

    /** Advance the weather by one step via the active strategy. */
    public void changeCurrentWeather()
    {
        weatherSystem.step();
    }

    /**
     * Force the weather to a specific state (e.g., on simulation reset).
     * @param weather the state to set
     */
    public void setCurrentWeather(Weather weather)
    {
        weatherSystem.set(weather);
    }

    /** @return the current weather state */
    public Weather getCurrentWeather()
    {
        return weatherSystem.getCurrent();
    }

    /**
     * @return the population counter for the given animal class, or null if
     *         no count has been recorded yet
     */
    public Counter getCurrentStats(Class forAnimal)
    {
        return counters.get(forAnimal);
    }

    /**
     * Replace the population counters (called each step by {@link Simulator}).
     * @param counter the new counters map
     */
    public void setCurrentStats(Map<Class, Counter> counter)
    {
        counters = counter;
    }

    /**
     * Returns a 0–1 value combining the time-of-day and weather effects.
     * A value of 1 means full activity; lower values suppress animal actions.
     * @return the aggregated probability multiplier for this step
     */
    public double getAggregatedProbabilityReduction()
    {
        return (currentNormalisedTime + weatherSystem.getCurrent().getActivityReduction()) / 2;
    }
}
