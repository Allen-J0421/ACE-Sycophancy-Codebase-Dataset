/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {
    private static final double DEFAULT_SIZE = 1.00;
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final PlantTraits TRAITS = new PlantTraits(
            10.0, 25, 16, 2, 0.15, 0.25, 1.2, WeatherType.RAIN, WeatherType.SUN);

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(false, foodValue, size, randomAge, field, location, TRAITS);
    }

    /**
     * Constructor for Grass using the species default size and food value.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(boolean randomAge, Field field, Location location) {
        this(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, randomAge, field, location);
    }

    /**
     * Create a new instance of Grass.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Grass instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Grass(true, field, location);
    }

}
