import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 * This class models weather in the simulation. 
 * It stores information about the current weather. 
 *
 * @version 2022.03.02
 */
public class Weather
{
    private static final int STATIC_WEATHER_DURATION = 12;
    private static final int MAX_RANDOM_WEATHER_DURATION = 36;

    private WeatherType currentWeather;
    private static final Random rand = Randomizer.getRandom();
    private boolean randomDuration = false;
    private final List<WeatherType> weatherList = Arrays.asList(WeatherType.RAINING, WeatherType.SUNNY, WeatherType.CLOUDY);
    
    
    /**
     * Create a new instance of weather. 
     */
    public Weather()
    {
       reset();
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
        updateForStep(steps);
    }

    /**
     * Updates the weather for the given simulation step.
     */
    public void updateForStep(int steps)
    {
        int duration = getCurrentDuration();
        if(steps % duration == 0) {
            changeWeather();
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
     * Changes the weather randomly. 
     */
    public void changeWeather()
    {
        currentWeather = weatherList.get(rand.nextInt(weatherList.size()));
    }

    /**
     * Reset the weather to the default state.
     */
    public void reset()
    {
        currentWeather = WeatherType.SUNNY;
    }

    private int getCurrentDuration()
    {
        if(randomDuration) {
            return rand.nextInt(MAX_RANDOM_WEATHER_DURATION) + 1;
        }
        return STATIC_WEATHER_DURATION;
    }
}
