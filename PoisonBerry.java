/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An PoisonBerry plant present in the simulation.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {
    private static final double DEFAULT_SIZE = 1.00;
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final PlantTraits TRAITS = new PlantTraits(
            10.0, 20, 16, 3, 0.104, 0.2, 1.2, WeatherType.RAIN, WeatherType.SNOW);

    /**
     * Constructor for a plant in the simulation.
     *
     * @param foodValue The food value of this plant.
     * @param size The initial size of this plant.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public PoisonBerry(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(true, foodValue, size, randomAge, field, location, TRAITS);
    }

    /**
     * Constructor for a PoisonBerry using the species default size and food value.
     *
     * @param randomAge Whether the berry should have a random age or not.
     * @param field The field in which the berry resides.
     * @param location The location in which the berry is spawned into.
     */
    public PoisonBerry(boolean randomAge, Field field, Location location) {
        this(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, randomAge, field, location);
    }

    /**
     * Create a new instance of this berry.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new PoisonBerry instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new PoisonBerry(true, field, location);
    }

}
