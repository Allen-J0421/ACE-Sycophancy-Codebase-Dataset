import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
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
    private int duration; // to keep track of when weather changes (every X hours)
    private boolean randomDuration = false;
    private final Set<WeatherType> weatherList = EnumSet.of(
            WeatherType.RAINING,
            WeatherType.SUNNY,
            WeatherType.CLOUDY
    );
    
    
    /**
     * Create a new instance of weather. 
     */
    public Weather()
    {
       currentWeather = WeatherType.SUNNY; 
       duration = 0;
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
     * If the duration is set to be random, the WeatherType lasts for 1-36 hours.
     * Otherwise, the WeatherType lasts for 12 hours.
     */
    public void checkWeatherChange(int steps)
    {
        if(randomDuration){
            duration = rand.nextInt(36) + 1;
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
     * Allows for the duration of the weather to be switched from random (1-36 steps) to static (12 steps).
     * @return The new state of the randomDuration boolean.
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
        WeatherType[] weatherOptions = weatherList.toArray(new WeatherType[0]);
        currentWeather = weatherOptions[rand.nextInt(weatherOptions.length)];
    }

    /**
     * Reset the weather to its initial state.
     */
    public void reset()
    {
        currentWeather = WeatherType.SUNNY;
        duration = 0;
        randomDuration = false;
    }
}
