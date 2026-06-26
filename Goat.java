import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Goat extends Prey {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.3065, 3, 10, 150, 0.1, 0.001);

    private static final int DEFAULT_FOOD_VALUE = 5;

    /**
     * Constructor for a Goat in the simulation.
     *
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field The field in which this goat resides.
     * @param location The location in which this goat is spawned into.
     */
    public Goat(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of goat.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new goat instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Goat(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Method for what the goat does, i.e. what is always run at every step.
     *
     * @param newGoats A list of all newborn goats in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newGoats, Weather weather, TimeOfDay time) {
        actAsPrey(newGoats, time, TimeOfDay.LATE_MORNING, 0.8);
    }
}
