import java.util.*;
/**
 * A singleton Weather object that will hold different types
 * of weather. Each different weather has their own modifier
 * that will affect how animal's behave, notably whether
 * they are successful in finding food.
 *
 * @version 2022.03.02
 */
public class Weather
{
    // A shared random object
    private static final Random rand = Randomizer.getRandom();
    
    // Hashmap that contain the weather and the hunting modifier for that weather
    private HashMap<String, Double> weatherAndModifier = new HashMap<String, Double>();
    // The current weather of the Weather object
    private String currentWeather;
    // Singleton instance of the Weather object
    private static Weather instance;
    
    public Weather() {
        weatherAndModifier.put("Sunny", 1.0);
        weatherAndModifier.put("Rainy", 0.7);
        weatherAndModifier.put("Foggy", 0.5);
    }
    
    public static Weather getWeather() {
        if (instance == null) {
            instance = new Weather();
        }
        return instance;
    }
    
    // Accessor and mutator methods
    
    /**
     * Return the current weather
     * 
     * @return String of the current weather
     */
    public String getCurrentWeather() {
        return currentWeather;
    }
    
    /**
     * Returns the hunting modifier of the current weather
     * 
     * @return double representing the hunting modifier
     */
    public double getWeatherModifier() {
        return weatherAndModifier.get(currentWeather);
    }
    
    /**
     * Set the current weather explicitly
     * 
     * @param String the desired weather
     */
    public void setWeather(String newWeather) {
        currentWeather = newWeather;
    }
    
    // Functional methods
    
    /**
     * Resets the current weather to "Sunny"
     */
    public void resetWeather() {
        currentWeather = "Sunny";
    }
    
    /**
     * Changes the weather of the Weather object to a random state
     */
    public void changeWeather() {
        Object[] weatherKeys = weatherAndModifier.keySet().toArray();
        int randomNumber = rand.nextInt(weatherKeys.length);
        currentWeather = (String) weatherKeys[randomNumber];
    }
}
