/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Herbivore {
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final HerbivoreTraits TRAITS = new HerbivoreTraits(
            Zebra.class, 0.305, 3, 10, 150, 0.1, 0.001, TimeOfDay.AROUND_MIDNIGHT, 0.9);

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field The field in which this zebra resides.
     * @param location The location in which this zebra is spawned into.
     */
    public Zebra(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location, TRAITS);
    }

    /**
     * Create a new instance of zebra.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new zebra instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Zebra(DEFAULT_FOOD_VALUE, true, field, location);
    }
}
