import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Prey {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.305, 3, 10, 150, 0.1, 0.001);

    private static final int DEFAULT_FOOD_VALUE = 5;

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field The field in which this zebra resides.
     * @param location The location in which this zebra is spawned into.
     */
    public Zebra(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
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

    /**
     * Method for what the zebra does, i.e. what is always run at every step.
     *
     * @param newZebras A list of all newborn zebras in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newZebras, Weather weather, TimeOfDay time) {
        actAsPrey(newZebras, time, TimeOfDay.AROUND_MIDNIGHT, 0.9);
    }
}
