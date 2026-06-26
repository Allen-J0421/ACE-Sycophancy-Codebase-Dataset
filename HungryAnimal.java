import java.util.List;
import java.util.Random;

/**
 * Shared behavior for animals that track hunger and hunt for food.
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
     * @param field The field in which this animal resides.
     * @param location The location in which this animal spawns into.
     */
    public HungryAnimal(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Shared hunting turn for predator and scavenger animals.
     *
     * @param newAnimals Newly born organisms from this turn.
     * @param time The current simulation time.
     * @param restingTime The time of day when this animal stops acting.
     */
    @Override
    public void act(List<Entity> newAnimals, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();

        if (!isAlive()) {
            return;
        }

        giveBirth(newAnimals);

        if (time == restingTime()) {
            return;
        }

        if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
            remove();
            return;
        }

        Location newLocation;
        if (rand.nextDouble() <= getDiseaseSpreadProbability()) {
            newLocation = findAnimalToInfect();
        } else {
            newLocation = findFood();
        }

        if (newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if (newLocation != null) {
            setLocation(newLocation);
        } else {
            remove();
        }
    }

    /**
     * The time of day when this animal does not act.
     *
     * @return The resting time.
     */
    protected abstract TimeOfDay restingTime();

    /**
     * Increment the food level of this animal by a given amount.
     *
     * @param foodLevel The value to increment food level by.
     */
    protected void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this animal more hungry. This could result in death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
