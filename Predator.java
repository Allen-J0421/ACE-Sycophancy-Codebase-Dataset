import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A predator animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Predator extends HungryAnimal {

    // define fields
    // Tuning shared by all predators; individual species override only the
    // values that genuinely differ (see getMaxAge / getBreedingAge / getInactiveTime).
    private static final double BREEDING_PROBABILITY = 0.115;
    private static final double EATING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 2;
    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.01;
    // The food level given to newborn predators.
    protected static final int DEFAULT_FOOD_LEVEL = 19;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a predator in the simulation.
     *
     * @param foodLevel The food level of this predator.
     * @param randomAge Whether the predator should have a random age or not.
     * @param field The field in which the predator resides.
     * @param location The location in which the predator spawns into.
     */
    public Predator(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Finds the nearest live prey, attempts to eat it, and returns its location.
     *
     * @return Location of food source.
     */
    @Override
    public Location findFood() {
        return huntLivingFood(Prey.class);
    }

    /**
     * Getter method to return this predator's probability of eating if food is found.
     *
     * @return The predator's eating probability.
     */
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

    /**
     * Getter method for the probability to breed of the predator.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Getter method for the maximum litter size of the predator's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Getter method to return this predator's disease spreading probability.
     *
     * @return The predator's disease spreading probability.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return SPREAD_DISEASE_PROBABILITY;
    }

    /**
     * Getter method to return the probability this predator dies from disease.
     *
     * @return The predator's disease death probability.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return DEATH_BY_DISEASE_PROBABILITY;
    }

    /**
     * Called when a predator either eats or leaves a prey it
     * has killed.
     *
     * @param consumable The prey to be eaten.
     * @return Whether the prey has been eaten or not.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if (rand.nextDouble() <= getEatingProbability()) {
            incrementFoodLevel(consumable.getFoodValue());
            consumable.setEaten();
            //System.out.println("EATEN PREY");
            return true;
        } else {
            //System.out.println("LEFT PREY");
            return false;
        }
    }
}
