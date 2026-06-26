/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Elephant extends Herbivore {
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final HerbivoreTraits TRAITS = new HerbivoreTraits(
            Elephant.class, 0.3, 3, 10, 150, 0.1, 0.001, TimeOfDay.SUNSET, 0.85);

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location, TRAITS);
    }

    /**
     * Constructor for an Elephant using the species default food value.
     *
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(boolean randomAge, Field field, Location location) {
        this(DEFAULT_FOOD_VALUE, randomAge, field, location);
    }

    /**
     * Create a new instance of Elephant.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Elephant instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Elephant(true, field, location);
    }
}
