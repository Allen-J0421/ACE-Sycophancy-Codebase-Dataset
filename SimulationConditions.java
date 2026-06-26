/**
 * Immutable environmental conditions for one simulation step.
 */
public class SimulationConditions
{
    private final boolean night;
    private final int temperature;
    private final boolean yearPassed;

    public SimulationConditions(boolean night, int temperature, boolean yearPassed)
    {
        this.night = night;
        this.temperature = temperature;
        this.yearPassed = yearPassed;
    }

    public boolean isNight()
    {
        return night;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public boolean hasYearPassed()
    {
        return yearPassed;
    }
}
