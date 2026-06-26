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

    private static final WeatherRule[] WEATHER_RULES = {
            new WeatherRule(WeatherType.SUN, 0.2),
            new WeatherRule(WeatherType.RAIN, 0.1),
            new WeatherRule(WeatherType.FOG, 0.3),
            new WeatherRule(WeatherType.SNOW, 0.05),
            new WeatherRule(WeatherType.STORM, 0.02)
    };

    private static final int RECENT_WEATHER_HISTORY_LIMIT = 3;
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
                count = 0; // reset
            }
            return;
        }

        this.hours = rand.nextInt(MAX_HOURS);

        type = chooseNextWeather();
        recentWeather.add(type);

        if (recentWeather.size() > RECENT_WEATHER_HISTORY_LIMIT) {
            recentWeather.remove(0);
        }
        count++;
    }

    /**
     * Returns recent weather history.
     * @return Recent weather history.
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

    /**
     * Select the next weather event while preserving the original ordered probability checks.
     *
     * @return The chosen weather type.
     */
    private WeatherType chooseNextWeather() {
        for (WeatherRule rule : WEATHER_RULES) {
            if (rand.nextDouble() <= rule.getProbability()) {
                return rule.getType();
            }
        }
        return type;
    }

    /**
     * Configuration for one weather transition option.
     */
    private static final class WeatherRule {
        private final WeatherType type;
        private final double probability;

        WeatherRule(WeatherType type, double probability) {
            this.type = type;
            this.probability = probability;
        }

        WeatherType getType() {
            return type;
        }

        double getProbability() {
            return probability;
        }
    }
}
