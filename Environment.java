/**
 * This class models the environmental factors that affect the simulation
 * such as the weather and time.
 *
 * @version 2022.03.3
 */

public class Environment {
    private final Time time;
    private final Weather weather;

    /**
     * Create a new instance of class Environment.
     */
    public Environment(Time time, Weather weather)
    {
        this.time = time;
        this.weather = weather;
    }

    /**
     * Returns an instance of class Time.
     */
    public Time getTime()
    {
        return time;
    }

    /**
     * Returns an instance of class Weather.
     */
    public Weather getWeather()
    {
        return weather;
    }

}
