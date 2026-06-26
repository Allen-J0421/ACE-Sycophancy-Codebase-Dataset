/**
 * Immutable snapshot of the current weather condition.
 */
public class Weather
{
    private final WeatherType currentWeather;

    public Weather(WeatherType currentWeather)
    {
        this.currentWeather = currentWeather;
    }

    public WeatherType getCurrentWeather()
    {
        return currentWeather;
    }
}
