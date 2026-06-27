/**
 * Immutable state used to render a simulation frame.
 */
public final class SimulationDisplayContext
{
    private final int step;
    private final Field field;
    private final boolean timeOfDay;
    private final Weather weather;
    private final double oxygenLevel;

    public SimulationDisplayContext(int step, Field field, boolean timeOfDay, Weather weather, double oxygenLevel)
    {
        this.step = step;
        this.field = field;
        this.timeOfDay = timeOfDay;
        this.weather = weather;
        this.oxygenLevel = oxygenLevel;
    }

    public int getStep()
    {
        return step;
    }

    public Field getField()
    {
        return field;
    }

    public boolean isTimeOfDay()
    {
        return timeOfDay;
    }

    public Weather getWeather()
    {
        return weather;
    }

    public double getOxygenLevel()
    {
        return oxygenLevel;
    }
}
