import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    private static final AnimalProfile PROFILE = new AnimalProfile(0.115, 2, 26, 140, 0.01, 0.01);
    private static final double EATING_PROBABILITY = 0.6;

    private static final int DEFAULT_FOOD_LEVEL = 19;

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Create a cheetah with default starting values.
     *
     * @param field The field in which the cheetah will reside.
     * @param location The location in which the cheetah will occupy.
     * @return A new Cheetah instance.
     */
    static Cheetah createDefault(Field field, Location location) {
        return new Cheetah(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    @Override
    protected AnimalProfile getProfile() {
        return PROFILE;
    }

    /**
     * Create a new instance of Cheetah.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Cheetah instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return createDefault(field, location);
    }

    /**
     * Method for what the cheetah does, i.e. what is always run at every step.
     *
     * @param newCheetahs A list of all newborn cheetahs in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newCheetahs, Weather weather, TimeOfDay time) {
        actAsPredator(newCheetahs, time, TimeOfDay.EARLY_AFTERNOON);
    }

    /**
     * Getter method to return this cheetah's probability of eating if food is found.
     *
     * @return The cheetah's eating probability.
     */
    @Override
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

}
