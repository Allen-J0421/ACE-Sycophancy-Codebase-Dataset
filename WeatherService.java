import java.util.List;
import java.util.Random;

/**
 * Centralizes weather state, duration, and weather-driven simulation rules.
 */
public class WeatherService
{
    private static final int FIXED_WEATHER_DURATION = 12;
    private static final int MAX_RANDOM_DURATION = 36;
    private static final double BASE_GRASS_GERMINATION_RATE = 0.1;
    private static final double SUNNY_GRASS_GERMINATION_RATE = 0.15;
    private static final List<WeatherType> WEATHER_TYPES = List.of(
            WeatherType.RAINING,
            WeatherType.SUNNY,
            WeatherType.CLOUDY
    );

    private final RandomProvider randomProvider;

    private Weather currentWeather;
    private int remainingDuration;
    private boolean randomDurationEnabled;

    public WeatherService(RandomProvider randomProvider)
    {
        this.randomProvider = randomProvider;
        reset();
    }

    public Weather getCurrentWeather()
    {
        return currentWeather;
    }

    public WeatherType getCurrentWeatherType()
    {
        return currentWeather.getCurrentWeather();
    }

    public void advance()
    {
        remainingDuration--;
        if(remainingDuration <= 0) {
            currentWeather = new Weather(getRandomWeatherType());
            remainingDuration = getNextDuration();
        }
    }

    public void reset()
    {
        currentWeather = new Weather(WeatherType.SUNNY);
        remainingDuration = getNextDuration();
    }

    public boolean toggleRandomWeatherLength()
    {
        randomDurationEnabled = !randomDurationEnabled;
        remainingDuration = getNextDuration();
        return randomDurationEnabled;
    }

    public boolean canCreateGrassPatches()
    {
        return getCurrentWeatherType() == WeatherType.RAINING;
    }

    public boolean allowsGrassGermination()
    {
        return getCurrentWeatherType() != WeatherType.CLOUDY;
    }

    public double getGrassGerminationRate()
    {
        if(getCurrentWeatherType() == WeatherType.SUNNY) {
            return SUNNY_GRASS_GERMINATION_RATE;
        }
        return BASE_GRASS_GERMINATION_RATE;
    }

    public boolean allowsEagleForaging()
    {
        return getCurrentWeatherType() != WeatherType.RAINING;
    }

    private int getNextDuration()
    {
        if(randomDurationEnabled) {
            return randomProvider.getRandom().nextInt(MAX_RANDOM_DURATION) + 1;
        }
        return FIXED_WEATHER_DURATION;
    }

    private WeatherType getRandomWeatherType()
    {
        Random rand = randomProvider.getRandom();
        return WEATHER_TYPES.get(rand.nextInt(WEATHER_TYPES.size()));
    }
}
