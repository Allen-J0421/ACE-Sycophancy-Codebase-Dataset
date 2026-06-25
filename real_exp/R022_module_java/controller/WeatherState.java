package controller;

import java.util.Random;

import field.Field;
import utils.RandomCollection;

/**
 * Handles which state (Sunny, Rain, Normal) to set the current weather state to and how long.
 * This is implemented as a Singleton.
 *
 */
public class WeatherState {
    private Field field;
    private Random random;
    private Weather currentWeather;
    private RandomCollection<Weather> weightedCollection;
    
    // min and max durations of each state
    private int MIN_WEATHER_DURATION = 40, MAX_WEATHER_DURATION = 50;

    private int finalStep;
    private int duration;

    private static WeatherState instance;

    /**
     * Getter for the singleton instance.
     * @return the singleton instance.
     */
    public static WeatherState getInstance() {
        if (instance == null) {
            instance = new WeatherState(); 
        }

        return instance;
    }

    /**
     * Constructor for Weather State.
     * Choose the initial weather state and initialise the collection holding the possible weather states
     * along with their weights.
     */
    private WeatherState() {
        currentWeather = Weather.NORMAL;
        field = Field.getInstance();
        random = new Random();
        weightedCollection = new RandomCollection<>();

        finalStep = random.nextInt(MAX_WEATHER_DURATION - MIN_WEATHER_DURATION) + MIN_WEATHER_DURATION;
        duration = finalStep;

        for (Weather weather : Weather.values()) {
            weightedCollection.add(weather.getWeight(), weather);
        }
    }

    /**
     * Check whether it is the end of the current weather state and set a new
     * weather state if it is.
     */
    public void updateWeather() {
        int step = field.getStep();
        if (step >= finalStep) {
            duration = random.nextInt(MAX_WEATHER_DURATION - MIN_WEATHER_DURATION) + MIN_WEATHER_DURATION;
            finalStep = step + duration;
            currentWeather = weightedCollection.next();
        }
    }

    public void reset() {
        currentWeather = Weather.NORMAL;
        finalStep = random.nextInt(MAX_WEATHER_DURATION - MIN_WEATHER_DURATION) + MIN_WEATHER_DURATION;
        duration = finalStep;   
    }


    /* Getters */

    /**
     * @return the current weather stat
     */
    public Weather getWeather() {
        return currentWeather;
    }

    /**
     * @return how long the current weather state should last
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return at which step should the current weather state end
     */
    public int getFinalStep() {
        return finalStep;
    }
}
