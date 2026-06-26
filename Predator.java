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

    private static final Random rand = Randomizer.getRandom();

    private int foodLevel;

    /**
     * Constructor for a predator in the simulation.
     *
     * @param traits    Species-level configuration for this predator.
     * @param foodLevel The initial food level of this predator.
     * @param randomAge Whether the predator should have a random age or not.
     * @param field     The field in which the predator resides.
     * @param location  The location in which the predator spawns into.
     */
    public Predator(AnimalTraits traits, int foodLevel, boolean randomAge, Field field, Location location) {
        super(traits, randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Performs one simulation step: ages, hungers, breeds, spreads disease, hunts, and moves.
     * The predator skips acting entirely during its rest period.
     */
    @Override
    public void act(List<Organism> newOrganisms, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newOrganisms);
            if (time == getRestTime()) return;
            if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
                remove();
                return;
            }
            Location newLocation = rand.nextDouble() <= getDiseaseSpreadProbability()
                ? findAnimalToInfect() : findFood();
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                remove();
            }
        }
    }

    /**
     * Searches adjacent cells for a live Prey to kill and eat.
     *
     * @return The location of the eaten prey, or null if none found.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Organism obj = field.getObjectAt(where);
            if (obj instanceof Prey) {
                Prey prey = (Prey) obj;
                if (prey.isAlive()) {
                    prey.setDead();
                    return eat(prey) ? where : null;
                }
            }
        }
        return null;
    }

    /**
     * Returns the probability this predator eats a kill rather than leaving it.
     */
    abstract public double getEatingProbability();

    /**
     * Eats a killed prey with probability given by {@link #getEatingProbability()}.
     *
     * @param consumable The prey to be eaten.
     * @return Whether the prey was consumed.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if (rand.nextDouble() <= getEatingProbability()) {
            incrementFoodLevel(consumable.getFoodValue());
            consumable.setEaten();
            return true;
        }
        return false;
    }

    /**
     * Increase the predator's food level by a given amount.
     */
    public void incrementFoodLevel(int amount) {
        this.foodLevel += amount;
    }

    /**
     * Decrease hunger by one step; removes the predator if food level reaches zero.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
