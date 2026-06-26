/**
 * Immutable snapshot of the environment visible to actors during one simulation tick.
 */
public final class SimulationStep
{
    private final Weather weather;
    private final DayState dayState;

    /**
     * Create a simulation step context.
     *
     * @param weather the current weather.
     * @param dayState the current day state.
     */
    public SimulationStep(Weather weather, DayState dayState)
    {
        this.weather = weather;
        this.dayState = dayState;
    }

    /**
     * @return the weather for this step.
     */
    public Weather getWeather()
    {
        return weather;
    }

    /**
     * @return the day state for this step.
     */
    public DayState getDayState()
    {
        return dayState;
    }
}
