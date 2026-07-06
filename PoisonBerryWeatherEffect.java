/**
 * This file is part of the Predator-Prey Simulation.
 *
 * WeatherEffect strategy for PoisonBerry: breeding probability is boosted when
 * recent weather includes rain or snow.
 *
 * @version 2022.03.02
 */
public class PoisonBerryWeatherEffect implements WeatherEffect {

    private static final double LOW_BREEDING_PROBABILITY = 0.104;
    private static final double HIGH_BREEDING_PROBABILITY = 0.2;

    @Override
    public double getBreedingProbability(Weather weather) {
        if (weather.getRecentWeather().contains(WeatherType.RAIN) ||
                weather.getRecentWeather().contains(WeatherType.SNOW)) {
            return HIGH_BREEDING_PROBABILITY;
        }
        return LOW_BREEDING_PROBABILITY;
    }
}
