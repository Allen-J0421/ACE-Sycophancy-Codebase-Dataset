import java.util.*;
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
    private Set<WeatherType> weatherList;


    /**
     * Create a new instance of weather.
     */
    public Weather()
    {
       weatherList = new HashSet<>();
       addWeatherList();
       currentWeather = WeatherType.SUNNY;
    }

    /**
     * Add the types of weather conditions to a HashSet.
     */
    public void addWeatherList()
    {
        weatherList = new HashSet<>(Arrays.asList(WeatherType.RAINING, WeatherType.SUNNY, WeatherType.CLOUDY));
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
     * Changes the weather randomly. 
     */
    public void changeWeather()
    {
        int randomInt = rand.nextInt(weatherList.size());
        currentWeather = (WeatherType) weatherList.toArray()[randomInt];
    }
}
