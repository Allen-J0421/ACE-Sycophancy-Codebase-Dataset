import java.util.Random;

/**
 * Default {@link WeatherSystem} implementation.
 *
 * On each {@link #step()} call, transitions to a uniformly-random weather
 * state with probability {@code CHANGE_PROBABILITY}; otherwise the current
 * state is retained.
 */
class RandomWeatherSystem implements WeatherSystem
{
    private static final double CHANGE_PROBABILITY = 0.01;
    private static final Random rand = Randomizer.getRandom();

    private Weather current;

    /**
     * @param initial the starting weather state
     */
    RandomWeatherSystem(Weather initial)
    {
        this.current = initial;
    }

    @Override
    public Weather getCurrent()
    {
        return current;
    }

    @Override
    public void set(Weather weather)
    {
        current = weather;
    }

    @Override
    public void step()
    {
        if (rand.nextDouble() <= CHANGE_PROBABILITY) {
            current = Weather.values()[rand.nextInt(Weather.values().length)];
        }
    }
}
