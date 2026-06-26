import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Abstract base for animals that maintain a food level and starve when it
 * reaches zero. Predators and Scavengers share identical act(), foodLevel,
 * incrementFoodLevel(), and incrementHunger() — all are defined once here.
 *
 * @version 2022.03.02
 */
public abstract class HungryAnimal extends Animal {

    protected static final Random rand = Randomizer.getRandom();

    private int foodLevel;

    /**
     * Constructor for a HungryAnimal.
     *
     * @param traits    Species-level configuration.
     * @param foodLevel Initial food level.
     * @param randomAge Whether to assign a random age.
     * @param field     The field in which this animal resides.
     * @param location  The location in which this animal spawns into.
     */
    public HungryAnimal(AnimalTraits traits, int foodLevel, boolean randomAge,
                        Field field, Location location) {
        super(traits, randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Performs one simulation step: ages, hungers, breeds, spreads disease,
     * searches for food, and moves. Skips acting entirely during rest period.
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

    /** Increases food level by the given amount. */
    protected void incrementFoodLevel(int amount) {
        foodLevel += amount;
    }

    /** Decreases food level by one; removes this animal if it reaches zero. */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
