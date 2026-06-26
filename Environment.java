/**
 * This class models the environmental factors that affect the simulation
 * such as the weather and time.
 *
 * @version 2022.03.3
 */

public class Environment {
    private final Time time;
    private final SimulationContext context;

    /**
     * Create a new instance of class Environment.
     */
    public Environment(Time time, SimulationContext context)
    {
        this.time = time;
        this.context = context;
    }

    /**
     * Returns an instance of class Time.
     */
    public Time getTime()
    {
        return time;
    }

    /**
     * Returns the current weather snapshot.
     */
    public Weather getWeather()
    {
        return context.getWeatherService().getCurrentWeather();
    }

    public WeatherService getWeatherService()
    {
        return context.getWeatherService();
    }

    public void advanceTime()
    {
        time.incrementTime();
    }

    public void reset()
    {
        time.reset();
    }

}
