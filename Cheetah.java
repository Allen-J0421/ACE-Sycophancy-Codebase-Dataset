/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {
    private static final int DEFAULT_FOOD_LEVEL = 19;
    private static final PredatorTraits TRAITS = new PredatorTraits(
            Cheetah.class, 0.115, 2, 26, 140, 0.01, 0.01, 0.6, TimeOfDay.EARLY_AFTERNOON);

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location, TRAITS);
    }

    /**
     * Constructor for a Cheetah using the species default food level.
     *
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(boolean randomAge, Field field, Location location) {
        this(DEFAULT_FOOD_LEVEL, randomAge, field, location);
    }

    /**
     * Create a new instance of Cheetah.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Cheetah instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Cheetah(true, field, location);
    }
}
