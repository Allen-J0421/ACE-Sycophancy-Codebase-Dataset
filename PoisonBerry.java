/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An PoisonBerry plant present in the simulation.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {

    private static final PlantAttributes ATTRIBUTES =
            new PlantAttributes(20, 16, 3, 10.0, 0.104, 0.2, 1.0, 1.2, 5, true);

    /**
     * Constructor for a plant in the simulation.
     *
     * @param foodValue The food value of this plant.
     * @param size The initial size of this plant.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public PoisonBerry(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, PoisonBerry::new);
    }

    @Override
    protected boolean hasFavorableWeather(Weather weather) {
        return weather.getRecentWeather().contains(WeatherType.RAIN)
                || weather.getRecentWeather().contains(WeatherType.SNOW);
    }

}
