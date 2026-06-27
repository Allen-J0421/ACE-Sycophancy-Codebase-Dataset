import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
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
    private static final String SUNNY = "Sunny";
    private static final String RAINY = "Rainy";
    private static final String FOGGY = "Foggy";
    // A shared random object
    private static final Random rand = Randomizer.getRandom();

    // Hashmap that contain the weather and the hunting modifier for that weather
    private final Map<String, Double> weatherAndModifier;
    // The current weather of the Weather object
    private String currentWeather;
    // Singleton instance of the Weather object
    private static Weather instance;

    public Weather() {
        weatherAndModifier = new LinkedHashMap<>();
        weatherAndModifier.put(SUNNY, 1.0);
        weatherAndModifier.put(RAINY, 0.7);
        weatherAndModifier.put(FOGGY, 0.5);
        resetWeather();
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
     * Set the current weather explicitly.
     *
     * @param newWeather the desired weather
     */
    public void setWeather(String newWeather) {
        currentWeather = newWeather;
    }

    // Functional methods
    
    /**
     * Resets the current weather to "Sunny"
     */
    public void resetWeather() {
        currentWeather = SUNNY;
    }

    /**
     * Changes the weather of the Weather object to a random state
     */
    public void changeWeather() {
        String[] weatherKeys = weatherAndModifier.keySet().toArray(new String[0]);
        int randomNumber = rand.nextInt(weatherKeys.length);
        currentWeather = weatherKeys[randomNumber];
    }
}
