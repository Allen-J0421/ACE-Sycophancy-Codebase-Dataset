/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A PoisonBerry plant present in the simulation.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {

    private static final PlantTraits TRAITS = new PlantTraits(
        /* maxAge                  */ 20,
        /* breedingAge             */ 16,
        /* maxSize                 */ 10.0,
        /* maxLitterSize           */ 3,
        /* growthRate              */ 1.2,
        /* lowBreedingProbability  */ 0.104,
        /* highBreedingProbability */ 0.2
    );

    private static final int    DEFAULT_FOOD_VALUE = 5;
    private static final double DEFAULT_SIZE       = 1.0;

    private static final WeatherType[] BOOST_WEATHER = { WeatherType.RAIN, WeatherType.SNOW };

    /**
     * Constructor for a PoisonBerry in the simulation.
     *
     * @param foodValue The food value of this berry when eaten.
     * @param size      The initial size of this berry.
     * @param randomAge Whether we assign this berry a random age or not.
     * @param field     The field in which this berry resides.
     * @param location  The location in which this berry is spawned into.
     */
    public PoisonBerry(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(TRAITS, true, foodValue, size, randomAge, field, location);
    }

    @Override
    protected WeatherType[] getBoostWeatherTypes() { return BOOST_WEATHER; }

    /** Creates a poison berry with a random age for initial population seeding. */
    public static PoisonBerry spawn(Field field, Location location) {
        return new PoisonBerry(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, true, field, location);
    }

    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new PoisonBerry(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, true, field, location);
    }
}
