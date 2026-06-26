import java.util.Objects;

/**
 * Coordinates environment state changes for each simulation step.
 */
public class EnvironmentController
{
    private final Environment environment;

    public EnvironmentController()
    {
        this(new Time(), new Weather());
    }

    public EnvironmentController(Time time, Weather weather)
    {
        environment = new Environment(
                Objects.requireNonNull(time, "time"),
                Objects.requireNonNull(weather, "weather"));
    }

    public Environment getEnvironment()
    {
        return environment;
    }

    public void advance(int simulationStep)
    {
        environment.getTime().advanceOneStep();
        environment.getWeather().updateForStep(simulationStep);
    }

    public void reset()
    {
        environment.getTime().reset();
        environment.getWeather().reset();
    }

    public WeatherType getCurrentWeather()
    {
        return environment.getWeather().getCurrentWeather();
    }

    public String getCurrentTimeString()
    {
        return environment.getTime().getCurrentTimeString();
    }

    public boolean isRaining()
    {
        return getCurrentWeather() == WeatherType.RAINING;
    }
}
