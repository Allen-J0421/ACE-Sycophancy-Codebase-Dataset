/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.115;
    private static final double EATING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 2;
    private static final int BREEDING_AGE = 26;
    private static final int MAX_AGE = 140;

    private static final int DEFAULT_FOOD_LEVEL = 19;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.01;

    // The cheetah rests in the early afternoon.
    private static final TimeOfDay INACTIVE_TIME = TimeOfDay.EARLY_AFTERNOON;

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
     * Getter method for the probability to breed of the cheetah.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the cheetah's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the cheetah.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the cheetah.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this cheetah's disease spreading probability.
     *
     * @return The cheetah's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this cheetah dies from disease.
     *
     * @return The cheetah's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Create a new instance of Cheetah.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Cheetah instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Cheetah(DEFAULT_FOOD_LEVEL, true, field, location);
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

    /**
     * Getter method for the time of day at which the cheetah rests.
     *
     * @return The TimeOfDay during which the cheetah is inactive.
     */
    @Override
    protected TimeOfDay getInactiveTime() {
        return INACTIVE_TIME;
    }
}
