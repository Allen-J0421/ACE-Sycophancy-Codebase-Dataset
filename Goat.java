/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Goat extends Herbivore {
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final HerbivoreTraits TRAITS = new HerbivoreTraits(
            Goat.class, 0.3065, 3, 10, 150, 0.1, 0.001, TimeOfDay.LATE_MORNING, 0.8);

    /**
     * Constructor for a Goat in the simulation.
     *
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field The field in which this goat resides.
     * @param location The location in which this goat is spawned into.
     */
    public Goat(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location, TRAITS);
    }

    /**
     * Constructor for a Goat using the species default food value.
     *
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field The field in which this goat resides.
     * @param location The location in which this goat is spawned into.
     */
    public Goat(boolean randomAge, Field field, Location location) {
        this(DEFAULT_FOOD_VALUE, randomAge, field, location);
    }

    /**
     * Create a new instance of goat.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new goat instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Goat(true, field, location);
    }
}
