/**
 * Immutable state shared across a single simulation step.
 */
public final class SimulationContext
{
    private final Field field;
    private final Weather weather;
    private final Disease disease;
    private final int step;
    private final double oxygenLevel;
    private final boolean timeOfDay;

    public SimulationContext(Field field, Weather weather, Disease disease, int step, double oxygenLevel, boolean timeOfDay)
    {
        this.field = field;
        this.weather = weather;
        this.disease = disease;
        this.step = step;
        this.oxygenLevel = oxygenLevel;
        this.timeOfDay = timeOfDay;
    }

    public Field getField()
    {
        return field;
    }

    public Weather getWeather()
    {
        return weather;
    }

    public Disease getDisease()
    {
        return disease;
    }

    public int getStep()
    {
        return step;
    }

    public double getOxygenLevel()
    {
        return oxygenLevel;
    }

    public boolean isTimeOfDay()
    {
        return timeOfDay;
    }
}
