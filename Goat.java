/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Goat extends Herbivore {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.3065;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;

    private static final int DEFAULT_FOOD_VALUE = 5;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.1;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

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

    /**
     * Getter method for the probability to breed of the goat.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the goat's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the goat.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the goat.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this goat's disease spreading probability.
     *
     * @return The goat's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this goat dies from disease.
     *
     * @return The goat's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
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
     * Checks all adjacent location for goats that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this goat can breed or not.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= getBreedingAge() && hasCompatibleMateNearby(Goat.class);
    }

    @Override
    protected TimeOfDay getReducedActivenessTime() {
        return TimeOfDay.LATE_MORNING;
    }

    @Override
    protected double getReducedActiveness() {
        return 0.8;
    }
}
