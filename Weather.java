import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * A singleton Weather object that will hold different types
 * of weather. Each different weather has their own modifier
 * that will affect how animals behave, notably whether
 * they are successful in finding food.
 *
 * @version 2022.03.02
 */
public class Weather
{
    private static final String SUNNY = "Sunny";
    private static final String RAINY = "Rainy";
    private static final String FOGGY = "Foggy";
    private static final String[] WEATHER_TYPES = { SUNNY, RAINY, FOGGY };
    // A shared random object
    private static final Random RAND = Randomizer.getRandom();

    // Weather names and their hunting modifiers.
    private final Map<String, Double> weatherModifiers;
    // The current weather of the Weather object
    private String currentWeather;
    // Singleton instance of the Weather object
    private static Weather instance;

    public Weather() {
        weatherModifiers = createWeatherModifiers();
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
        return weatherModifiers.get(currentWeather);
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
        int randomNumber = RAND.nextInt(WEATHER_TYPES.length);
        currentWeather = WEATHER_TYPES[randomNumber];
    }

    /**
     * Create the fixed lookup table for weather hunting modifiers.
     */
    private static Map<String, Double> createWeatherModifiers()
    {
        Map<String, Double> modifiers = new LinkedHashMap<>();
        modifiers.put(SUNNY, 1.0);
        modifiers.put(RAINY, 0.7);
        modifiers.put(FOGGY, 0.5);
        return modifiers;
    }
}
