import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A scavenger animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Scavenger extends Animal {

    private int foodLevel;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a scavenger in the simulation.
     *
     * @param traits    Species-level configuration for this scavenger.
     * @param foodLevel The initial food level of this scavenger.
     * @param randomAge Whether the scavenger should have a random age or not.
     * @param field     The field in which the scavenger resides.
     * @param location  The location in which the scavenger spawns into.
     */
    public Scavenger(AnimalTraits traits, int foodLevel, boolean randomAge, Field field, Location location) {
        super(traits, randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Performs one simulation step: ages, hungers, breeds, spreads disease, scavenges, and moves.
     * The scavenger skips acting entirely during its rest period.
     */
    @Override
    public void act(List<Entity> newOrganisms, Weather weather, TimeOfDay time) {
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
     * Searches adjacent cells for a dead Prey corpse to eat.
     *
     * @return The location of the consumed corpse, or null if none found.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Object obj = field.getObjectAt(where);
            if (obj instanceof Prey) {
                Prey prey = (Prey) obj;
                if (!prey.isAlive()) {
                    eat(prey);
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Unconditionally eats a dead prey corpse.
     *
     * @param consumable The corpse to be consumed.
     * @return true always (scavengers never leave their food).
     */
    @Override
    public boolean eat(Consumable consumable) {
        consumable.setEaten();
        incrementFoodLevel(consumable.getFoodValue());
        return true;
    }

    /**
     * Increase the scavenger's food level by a given amount.
     */
    public void incrementFoodLevel(int amount) {
        this.foodLevel += amount;
    }

    /**
     * Decrease hunger by one step; removes the scavenger if food level reaches zero.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
