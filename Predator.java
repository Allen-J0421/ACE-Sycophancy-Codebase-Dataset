import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A predator animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Predator extends Animal {

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

    private int foodLevel;

    /**
     * Constructor for a predator in the simulation.
     *
     * @param foodLevel The food level of this predator.
     * @param randomAge Whether the predator should have a random age or not.
     * @param field The field in which the predator resides.
     * @param location The location in which the predator spawns into.
     */
    public Predator(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.foodLevel = foodLevel;
    }

    /**
     * What the predator does at every step: age, get hungrier, breed, and -
     * unless it is resting - either spread disease or hunt for food and move.
     * This behaviour is shared by all predators; the only species-specific
     * variation is the time of day at which the predator rests.
     *
     * @param newPredators A list of all newborn predators in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newPredators, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if (!isAlive()) {
            return;
        }

        giveBirth(newPredators);

        // The predator rests (does nothing further) during its inactive time.
        if (time == getInactiveTime()) {
            return;
        }

        if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
            remove();
            return;
        }

        // Either spread disease to a neighbour or move towards a food source.
        Location newLocation;
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            newLocation = findAnimalToInfect();
        } else {
            newLocation = findFood();
        }

        if (newLocation == null) {
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if (newLocation != null) {
            setLocation(newLocation);
        } else {
            // Overcrowding.
            remove();
        }
    }

    /**
     * Getter method for the time of day at which this predator rests and
     * performs no further actions.
     *
     * @return The TimeOfDay during which the predator is inactive.
     */
    abstract protected TimeOfDay getInactiveTime();

    /**
     * Finds the nearest food source and returns its location.
     *
     * @return Location of food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();

            Object animal = field.getObjectAt(where);
            if(animal instanceof Prey) {
                Prey prey = (Prey) animal;
                if (prey.isAlive()) {
                    // kills animal
                    prey.setDead();
                    // random chance to eat
                    boolean eaten = eat(prey);

                    return eaten ? where : null;
                }
            }
        }
        return null;
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

    /**
     * Increase the predator's food level by a given integer amount.
     *
     * @param foodLevel The value to increment food level by.
     */
    public void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this predator more hungry. This could result in the predator's death.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
