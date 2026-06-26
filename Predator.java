/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A predator animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Predator extends HungryAnimal {

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
        super(traits, foodLevel, randomAge, field, location);
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
    public abstract double getEatingProbability();

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
}
