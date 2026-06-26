/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Vulture scavenger in the simulation.
 *
 * @version 2022.03.02
 */
public class Vulture extends Scavenger {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 12;
    private static final int MAX_AGE = 80;

    private static final int DEFAULT_FOOD_LEVEL = 40;

    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

    /**
     * Constructor for a vulture in the simulation.
     *
     * @param foodLevel The food level of this vulture.
     * @param randomAge Whether the vulture should have a random age or not.
     * @param field The field in which the vulture resides.
     * @param location The location in which the vulture spawns into.
     */
    public Vulture(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the vulture.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the vulture's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method for the maximum age of the vulture.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the vulture.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method to return this vulture's disease spreading probability.
     *
     * @return The vulture's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this vulture dies from disease.
     *
     * @return The vulture's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Create a new instance of Vulture.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Vulture instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Vulture(DEFAULT_FOOD_LEVEL, true, field, location);
    }
}
