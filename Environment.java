/**
 * This class models the environmental factors that affect the simulation
 * such as the weather and time.
 *
 * @version 2022.03.3
 */

public class Environment {
    private final Time time;
    private final WeatherService weatherService;

    /**
     * Create a new instance of class Environment.
     */
    public Environment(Time time, WeatherService weatherService)
    {
        this.time = time;
        this.weatherService = weatherService;
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
        return weatherService.getCurrentWeather();
    }

    public WeatherService getWeatherService()
    {
        return weatherService;
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
