package safari;

/**
 * Immutable view of the simulator state for rendering.
 */
public final class SimulationSnapshot
{
    private final Field field;
    private final int steps;
    private final int numberOfDays;
    private final boolean day;
    private final Weather weather;

    public SimulationSnapshot(Field field, int steps, int numberOfDays, boolean day, Weather weather)
    {
        this.field = field;
        this.steps = steps;
        this.numberOfDays = numberOfDays;
        this.day = day;
        this.weather = weather;
    }

    public Field getField()
    {
        return field;
    }

    public int getSteps()
    {
        return steps;
    }

    public int getNumberOfDays()
    {
        return numberOfDays;
    }

    public boolean isDay()
    {
        return day;
    }

    public Weather getWeather()
    {
        return weather;
    }
}
