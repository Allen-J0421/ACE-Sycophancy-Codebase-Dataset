/**
 * Owns the simulation's Environment and drives its state transitions.
 * SimulationEngine delegates all time and weather advancement here so that
 * the engine's step loop remains free of environment-internal details.
 *
 * @version 2022.03.02
 */
public class EnvironmentManager
{
    private final Environment environment;

    public EnvironmentManager()
    {
        this.environment = new Environment(new Time(), new Weather());
    }

    /**
     * Advance time by one step and check whether the weather should change.
     * @param step The current simulation step number (used by weather change logic).
     */
    public void tick(int step)
    {
        environment.getTime().incrementTime();
        environment.getWeather().checkWeatherChange(step);
    }

    /**
     * Reset time back to the start-of-simulation state.
     * Weather is not reset — it continues from wherever it was.
     */
    public void reset()
    {
        environment.getTime().reset();
    }

    /** Return the underlying Environment for passing to actors and view. */
    public Environment getEnvironment()
    {
        return environment;
    }
}
