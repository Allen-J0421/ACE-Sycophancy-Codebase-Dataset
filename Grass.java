/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {

    private static final PlantAttributes ATTRIBUTES =
            new PlantAttributes(25, 16, 2, 10.0, 0.15, 0.25, 1.0, 1.2, 5, false);

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Grass::new);
    }

    @Override
    protected boolean hasFavorableWeather(Weather weather) {
        return weather.getRecentWeather().contains(WeatherType.RAIN)
                || weather.getRecentWeather().contains(WeatherType.SUN);
    }

}
