/**
 * Immutable environmental conditions for one simulation step.
 */
public class SimulationConditions
{
    private final boolean night;
    private final int temperature;
    private final boolean yearPassed;
    private final boolean spring;
    private final boolean seasonKnown;

    public SimulationConditions(boolean night, int temperature, boolean yearPassed)
    {
        this(night, temperature, yearPassed, false, false);
    }

    public SimulationConditions(boolean night, int temperature, boolean yearPassed, boolean spring)
    {
        this(night, temperature, yearPassed, spring, true);
    }

    private SimulationConditions(boolean night, int temperature, boolean yearPassed, boolean spring, boolean seasonKnown)
    {
        this.night = night;
        this.temperature = temperature;
        this.yearPassed = yearPassed;
        this.spring = spring;
        this.seasonKnown = seasonKnown;
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

    public boolean isSpring()
    {
        return spring;
    }

    public boolean hasSeason()
    {
        return seasonKnown;
    }
}
