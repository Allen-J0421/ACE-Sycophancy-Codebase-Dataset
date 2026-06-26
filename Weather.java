import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A class for the weather in the simulation.
 *
 * @version 2022.03.02
 */
public class Weather {

    private static final double SUN_PROBABILITY = 0.2;
    private static final double RAIN_PROBABILITY = 0.1;
    private static final double FOG_PROBABILITY = 0.3;
    private static final double SNOW_PROBABILITY = 0.05;
    private static final double STORM_PROBABILITY = 0.02;

    private static final int MAX_HOURS = 3;

    private List<WeatherType> recentWeather;
    private WeatherType type;
    private int hours;
    private int count;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for Weather class.
     *
     * @param initialType Initial given weather.
     */
    public Weather(WeatherType initialType) {
        this.type = initialType;
        recentWeather = new ArrayList<>();
    }

    /**
     * Generates the next weather event to produce, on the
     * condition one is not already in progress.
     */
    public void generate() {
        if (count != 0) {
            count++;
            if (count > hours) {
                count = 0;
            }
            return;
        }

        this.hours = rand.nextInt(MAX_HOURS);

        double r = rand.nextDouble();
        if (r < SUN_PROBABILITY) {
            type = WeatherType.SUN;
        } else if (r < SUN_PROBABILITY + RAIN_PROBABILITY) {
            type = WeatherType.RAIN;
        } else if (r < SUN_PROBABILITY + RAIN_PROBABILITY + FOG_PROBABILITY) {
            type = WeatherType.FOG;
        } else if (r < SUN_PROBABILITY + RAIN_PROBABILITY + FOG_PROBABILITY + SNOW_PROBABILITY) {
            type = WeatherType.SNOW;
        } else if (r < SUN_PROBABILITY + RAIN_PROBABILITY + FOG_PROBABILITY + SNOW_PROBABILITY + STORM_PROBABILITY) {
            type = WeatherType.STORM;
        }

        recentWeather.add(type);
        if (recentWeather.size() == 4) {
            recentWeather.remove(0);
        }
        count++;
    }

    /**
     * Returns an unmodifiable view of the most recent weathers.
     * @return List of most recent weathers.
     */
    public List<WeatherType> getRecentWeather() {
        return Collections.unmodifiableList(recentWeather);
    }

    /**
     * Get the current type of weather.
     * @return Get current weather type.
     */
    public WeatherType getType() {
        return this.type;
    }
}
