import java.util.Random;

/**
 * A system that keeps track of the current weather, including
 * e.g. whether it is raining or not. This has an impact on
 * the actors within the simulation, e.g. plants spread slower
 * without water from the rain.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class WeatherSystem
{
    // Whether or not it is currently raining:
    private static boolean isRaining;
    // A random number generator to select new weather properties:
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Change to a new day and randomly select new weather
     * properties.
     */
    public static void changeToNextDay()
    {
        isRaining = rand.nextInt(2) == 1;
    }
    
    /**
     * @return Whether or not it is raining.
     */
    public static boolean getIsRaining() { return isRaining; }
} 