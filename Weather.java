import java.util.Random;
/**
 * This class models weather in the simulation.
 * It stores information about the current weather.
 *
 * @version 2022.03.02
 */
public class Weather
{
    private WeatherType currentWeather;
    private static final Random rand = Randomizer.getRandom();
    private boolean randomDuration = false;

    // All possible weather conditions, in a fixed (enum declaration) order. Using
    // an ordered array rather than a HashSet makes weather selection deterministic
    // and reproducible across JVM runs (a HashSet of enums iterates in
    // identity-hashCode order, which varies between processes).
    private static final WeatherType[] WEATHER_TYPES = WeatherType.values();


    /**
     * Create a new instance of weather.
     */
    public Weather()
    {
       currentWeather = WeatherType.SUNNY;
    }

    /**
     * Returns the current weather of the simulation.
     */
    public WeatherType getCurrentWeather()
    {
        return currentWeather;
    }

    /**
     * Increments the variable duration.
     * If the duration is set to be random, the WeatherType lasts for 0-36 hours
     * Otherwise, the WeatherType lasts for 12 hours
     */
    public void checkWeatherChange(int steps)
    {
        if(randomDuration){
            int duration = rand.nextInt(36);
            if(steps % duration==0){
                changeWeather();
            }
        }
        else{
            if (steps % 12 == 0){
                changeWeather();
            }
        }
    }

    /**
     * Allows for the duration of the weather to be switched from random (0-36 steps) to static (12 steps)
     * @return The new state of the randomDuration boolean
     */
    public boolean toggleRandomWeatherLength()
    {
        randomDuration = !randomDuration;
        return randomDuration;
    }

    /**
     * Changes the weather randomly, picking uniformly from the weather types in
     * their fixed declaration order.
     */
    public void changeWeather()
    {
        int randomInt = rand.nextInt(WEATHER_TYPES.length);
        currentWeather = WEATHER_TYPES[randomInt];
    }
}
