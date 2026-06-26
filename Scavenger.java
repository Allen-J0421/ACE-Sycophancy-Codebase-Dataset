import java.util.Iterator;
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

    // define fields
    private int foodLevel;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a scavenger in the simulation.
     *
     * @param foodLevel The food level of this scavenger.
     * @param randomAge Whether the scavenger should have a random age or not.
     * @param field The field in which the scavenger resides.
     * @param location The location in which the scavenger spawns into.
     */
    public Scavenger(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Returns the time of day when this scavenger rests and skips its turn.
     *
     * @return The TimeOfDay at which this scavenger is inactive.
     */
    abstract public TimeOfDay getRestTime();

    /**
     * Performs one simulation step: ages, hungers, breeds, spreads disease, scavenges, and moves.
     *
     * @param newScavengers A list to receive newborn organisms this step.
     * @param weather The current weather state.
     * @param time The current time of day.
     */
    @Override
    public void act(List<Entity> newScavengers, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newScavengers);

            if (time == getRestTime()) {
                return;
            }

            if (rand.nextDouble() <= getDeathByDiseaseProbability() ) {
                remove();
                return;
            }

            // Move towards a source of food if found.
            Location newLocation;

            if (rand.nextDouble() <= getDiseaseSpreadProbability() ) {
                newLocation = findAnimalToInfect();
            } else {
                newLocation = findFood();
            }

            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                //setDead();
                remove();
            }
        }
    }

    /**
     * Find a food source the scavenger would want to eat.
     * @return The location of the food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal instanceof Prey) {
                Prey prey = (Prey) animal;
                // eats animal if dead only
                if (!prey.isAlive()) {
                    //System.out.println("EATEN DEAD");
                    eat(prey);
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Eat the corpse of a dead prey.
     *
     * @param consumable The item to be eaten.
     * @return Whether the consumable was eaten or not.
     */
    @Override
    public boolean eat(Consumable consumable) {
        // the scavenger does not leave its prey
        consumable.setEaten();
        incrementFoodLevel(consumable.getFoodValue());
        return true;
    }

    /**
     * Increment the food level of this scavenger by a given amount.
     *
     * @param foodLevel A given food level.
     */
    public void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this scavenger more hungry. This could result in the scavenger's death.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
