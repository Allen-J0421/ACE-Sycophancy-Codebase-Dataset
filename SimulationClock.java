/**
 * Owns the simulation step counter and coordinates time/weather advancement.
 * Composes {@link Time} and {@link Weather} into a single {@link Environment}
 * that actors and observers can read via {@link #getEnvironment()}.
 * Extracted from SimulationEngine so all temporal concerns live in one place.
 *
 * @version 2022.03.02
 */
public class SimulationClock
{
    private int step;
    private final Time time;
    private final Weather weather;
    private final Environment environment;

    public SimulationClock()
    {
        step = 0;
        time = new Time();
        weather = new Weather();
        environment = new Environment(time, weather);
    }

    /**
     * Advance by one simulation step: increment the step counter, update the
     * time-of-day model, and check whether the weather should change.
     */
    public void advance()
    {
        step++;
        time.incrementTime();
        weather.checkWeatherChange(step);
    }

    /**
     * Reset to step 0 and reinitialise the time model.
     * Weather is not reset — it continues from wherever it was, matching original behaviour.
     */
    public void reset()
    {
        step = 0;
        time.reset();
    }

    /** Returns the current simulation step number. */
    public int getStep()
    {
        return step;
    }

    /**
     * Returns the live {@link Environment} view composed of this clock's
     * Time and Weather. Always the same instance — no allocation per call.
     */
    public Environment getEnvironment()
    {
        return environment;
    }
}
