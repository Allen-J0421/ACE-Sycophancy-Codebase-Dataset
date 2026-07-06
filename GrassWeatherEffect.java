/**
 * This file is part of the Predator-Prey Simulation.
 *
 * WeatherEffect strategy for Grass: breeding probability is boosted when
 * recent weather includes rain or sun.
 *
 * @version 2022.03.02
 */
public class GrassWeatherEffect implements WeatherEffect {

    private static final double LOW_BREEDING_PROBABILITY = 0.15;
    private static final double HIGH_BREEDING_PROBABILITY = 0.25;

    @Override
    public double getBreedingProbability(Weather weather) {
        if (weather.getRecentWeather().contains(WeatherType.RAIN) ||
                weather.getRecentWeather().contains(WeatherType.SUN)) {
            return HIGH_BREEDING_PROBABILITY;
        }
        return LOW_BREEDING_PROBABILITY;
    }
}
