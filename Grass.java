/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {

    private static final PlantTraits TRAITS = new PlantTraits(
        /* maxAge                  */ 25,
        /* breedingAge             */ 16,
        /* maxSize                 */ 10.0,
        /* maxLitterSize           */ 2,
        /* growthRate              */ 1.2,
        /* lowBreedingProbability  */ 0.15,
        /* highBreedingProbability */ 0.25
    );

    private static final int    DEFAULT_FOOD_VALUE = 5;
    private static final double DEFAULT_SIZE       = 1.0;

    private static final WeatherType[] BOOST_WEATHER = { WeatherType.RAIN, WeatherType.SUN };

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param foodValue The food value of this grass when eaten.
     * @param size      The initial size of this grass.
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field     The field in which this grass resides.
     * @param location  The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(TRAITS, false, foodValue, size, randomAge, field, location);
    }

    @Override
    protected WeatherType[] getBoostWeatherTypes() { return BOOST_WEATHER; }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Grass(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, true, field, location);
    }
}
