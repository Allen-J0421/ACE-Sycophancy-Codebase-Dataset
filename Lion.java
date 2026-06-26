import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Lion predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Lion extends Predator {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.115, 2, 32, 130, 0.01, 0.01);
    private static final double EATING_PROBABILITY = 0.6;

    private static final int DEFAULT_FOOD_LEVEL = 19;

    /**
     * Constructor for a lion in the simulation.
     *
     * @param foodLevel The food level the lion is at initially.
     * @param randomAge Whether we assign this lion a random age or not.
     * @param field The field in which this lion resides.
     * @param location The location in which this lion is spawned into.
     */
    public Lion(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Create a lion with default starting values.
     *
     * @param field The field in which the lion will reside.
     * @param location The location in which the lion will occupy.
     * @return A new lion instance.
     */
    static Lion createDefault(Field field, Location location) {
        return new Lion(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of Lion.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new lion instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return createDefault(field, location);
    }

    /**
     * Method for what the lion does, i.e. what is always run at every step.
     *
     * @param newLions A list of all newborn lions in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newLions, Weather weather, TimeOfDay time) {
        actAsPredator(newLions, time, TimeOfDay.NIGHT);
    }

    /**
     * Getter method to return this lion's probability of eating if food is found.
     *
     * @return The lion's eating probability.
     */
    @Override
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

}
