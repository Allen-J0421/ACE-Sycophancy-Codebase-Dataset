import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An animal that sustains itself by hunting: it carries a food level that
 * depletes each step and is replenished by eating, and it dies once that level
 * runs out. Both predators (which hunt live prey) and scavengers (which eat
 * carrion) are hungry animals; they differ only in what they consider food and
 * the time of day at which they rest.
 *
 * @version 2022.03.02
 */
public abstract class HungryAnimal extends Animal {

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    private int foodLevel;

    /**
     * Constructor for a hungry animal in the simulation.
     *
     * @param foodLevel The food level of this animal.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the animal resides.
     * @param location The location in which the animal spawns into.
     */
    public HungryAnimal(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * What a hungry animal does at every step: age, get hungrier, breed, and -
     * unless it is resting - either spread disease or hunt for food and move.
     * Subclasses vary only in what counts as food ({@link #findFood()}) and the
     * time of day at which they rest ({@link #getInactiveTime()}).
     *
     * @param newAnimals A list of all newborn animals in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newAnimals, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if (!isAlive()) {
            return;
        }

        giveBirth(newAnimals);

        // The animal rests (does nothing further) during its inactive time.
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
     * Getter method for the time of day at which this animal rests and performs
     * no further actions.
     *
     * @return The TimeOfDay during which the animal is inactive.
     */
    abstract protected TimeOfDay getInactiveTime();

    /**
     * Increase this animal's food level by a given integer amount.
     *
     * @param foodLevel The value to increment food level by.
     */
    public void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
