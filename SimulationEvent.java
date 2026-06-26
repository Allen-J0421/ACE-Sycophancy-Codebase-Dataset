/**
 * Event data published by the simulator after state changes.
 */
public class SimulationEvent
{
    private final Simulator source;
    private final int step;
    private final Field field;
    private final WeatherType weather;
    private final String timeText;

    public SimulationEvent(Simulator source, int step, Field field, WeatherType weather, String timeText)
    {
        this.source = source;
        this.step = step;
        this.field = field;
        this.weather = weather;
        this.timeText = timeText;
    }

    public Simulator getSource()
    {
        return source;
    }

    public int getStep()
    {
        return step;
    }

    public Field getField()
    {
        return field;
    }

    public WeatherType getWeather()
    {
        return weather;
    }

    public String getTimeText()
    {
        return timeText;
    }
}
