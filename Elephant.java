import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Elephant extends Prey {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.3, 3, 10, 150, 0.1, 0.001);

    private static final int DEFAULT_FOOD_VALUE = 5;

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of Elephant.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Elephant instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Elephant(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Method for what the elephant does, i.e. what is always run at every step.
     *
     * @param newElephants A list of all newborn elephants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newElephants, Weather weather, TimeOfDay time) {
        actAsPrey(newElephants, time, TimeOfDay.SUNSET, 0.85);
    }
}
