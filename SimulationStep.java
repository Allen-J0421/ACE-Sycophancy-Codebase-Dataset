/**
 * Immutable simulation state for a single step.
 */
public class SimulationStep
{
    private final int stepNumber;
    private final Weather weather;
    private final TimeOfDay timeOfDay;

    /**
     * Create a simulation step snapshot.
     */
    public SimulationStep(int stepNumber, Weather weather)
    {
        this.stepNumber = stepNumber;
        this.weather = weather;
        timeOfDay = TimeOfDay.fromStep(stepNumber);
    }

    /**
     * @return The current step number.
     */
    public int getStepNumber()
    {
        return stepNumber;
    }

    /**
     * @return The weather for this step.
     */
    public Weather getWeather()
    {
        return weather;
    }

    /**
     * @return The time of day for this step.
     */
    public TimeOfDay getTimeOfDay()
    {
        return timeOfDay;
    }

    /**
     * @return true when the current time period is night.
     */
    public boolean isNight()
    {
        return timeOfDay.isNight();
    }
}
