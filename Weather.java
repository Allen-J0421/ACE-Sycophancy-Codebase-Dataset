import java.util.Arrays;
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

    private static final int MAX_EVENT_DURATION_HOURS = 3;
    private static final int MAX_RECENT_WEATHER = 4;
    private static final Random RAND = Randomizer.getRandom();
    private static final WeatherSelector SELECTOR = new WeatherSelector(
            Arrays.asList(
                    new WeatherRule(WeatherType.SUN, 0.2),
                    new WeatherRule(WeatherType.RAIN, 0.1),
                    new WeatherRule(WeatherType.FOG, 0.3),
                    new WeatherRule(WeatherType.SNOW, 0.05),
                    new WeatherRule(WeatherType.STORM, 0.02)
            ),
            RAND
    );

    private final WeatherHistory recentWeather;
    private WeatherType type;
    private int activeDurationHours;
    private int elapsedHours;

    /**
     * Constructor for Weather class.
     *
     * @param initialType Initial given weather.
     */
    public Weather(WeatherType initialType) {
        this.type = initialType;
        this.recentWeather = new WeatherHistory(MAX_RECENT_WEATHER);
    }

    /**
     * Advances the current weather state by one hour tick.
     */
    public void advance() {
        if (hasActiveEvent()) {
            advanceActiveEvent();
            return;
        }

        startNextEvent();
    }

    /**
     * Returns recent weather events.
     *
     * @return Unmodifiable list of recent weathers.
     */
    public List<WeatherType> getRecentWeather() {
        return recentWeather.asList();
    }

    /**
     * Get the current type of weather.
     * @return Get current weather type.
     */
    public WeatherType getType() {
        return this.type;
    }

    private boolean hasActiveEvent() {
        return elapsedHours != 0;
    }

    private void advanceActiveEvent() {
        elapsedHours++;
        if (elapsedHours > activeDurationHours) {
            elapsedHours = 0;
        }
    }

    private void startNextEvent() {
        activeDurationHours = RAND.nextInt(MAX_EVENT_DURATION_HOURS);
        type = SELECTOR.selectNext(type);
        recentWeather.record(type);
        elapsedHours = 1;
    }
}
