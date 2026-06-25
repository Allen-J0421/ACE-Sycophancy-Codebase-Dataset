import java.util.Random;

/**
 * A class which is used to access the weather and handle changing
 * weather.
 *
 * @version 26/02/2022
 */
public class Weather {
    // Stores all of the possible weather types.
    public static enum WeatherType { Sunny, Rainy, Foggy, Clear, Cloudy }
    
    private static WeatherType currentWeather;
    
    private static int weatherLastsForSteps;
    private static final int MINIMUM_STEPS_WEATHER_LASTS_FOR = 27;
    
    // Stores a randomiser used for generating random weather.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * @return Returns the current weather.
     */
    public static WeatherType getWeather() 
    {
        if (currentWeather == null) 
        {
            currentWeather = pickRandomWeather();
        }
        
        return currentWeather;
    }
    
    /**
     * Updates the weather and picks a random weather after a
     * random amount of steps.
     */
    public static void updateWeather() 
    {
        if (weatherLastsForSteps == 0) 
        {
            currentWeather = pickRandomWeather();
        }
        else 
        {
            weatherLastsForSteps--;
        }
        
        // Sunny can only occur during daytime; clear can only occur at 
        // night.
        if (Time.isNight() && currentWeather.equals(WeatherType.Sunny)) 
        {
            currentWeather = currentWeather.Clear;
        }
        
        if (!Time.isNight() && currentWeather.equals(WeatherType.Clear)) 
        {
            currentWeather = currentWeather.Sunny;
        }
    }
    
    /**
     * @return Returns a random weather type.
     */
    private static WeatherType pickRandomWeather() 
    {
        int randomInt = rand.nextInt(WeatherType.values().length);
        
        weatherLastsForSteps = rand.nextInt(20) + MINIMUM_STEPS_WEATHER_LASTS_FOR;
        
        return WeatherType.values()[randomInt];
    }
}