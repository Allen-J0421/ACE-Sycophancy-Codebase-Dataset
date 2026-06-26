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

    private final Class<? extends Animal> speciesClass;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final int breedingAge;
    private final int maxAge;
    private final double diseaseSpreadProbability;
    private final double deathByDiseaseProbability;
    private final TimeOfDay restingTime;
    private int foodLevel;

    /**
     * Constructor for a hungry animal in the simulation.
     *
     * @param foodLevel The food level of this animal.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which this animal resides.
     * @param location The location in which this animal spawns into.
     */
    public HungryAnimal(int foodLevel, boolean randomAge, Field field, Location location,
                        Class<? extends Animal> speciesClass,
                        double breedingProbability, int maxLitterSize, int breedingAge, int maxAge,
                        double diseaseSpreadProbability, double deathByDiseaseProbability,
                        TimeOfDay restingTime) {
        super(randomAge, field, location);
        this.speciesClass = speciesClass;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.diseaseSpreadProbability = diseaseSpreadProbability;
        this.deathByDiseaseProbability = deathByDiseaseProbability;
        this.restingTime = restingTime;
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

        if (time == restingTime) {
            return;
        }

        if (rand.nextDouble() <= deathByDiseaseProbability) {
            remove();
            return;
        }

        Location newLocation;
        if (rand.nextDouble() <= diseaseSpreadProbability) {
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
     * @return The breeding probability for this species.
     */
    @Override
    public double getBreedingProbability() {
        return breedingProbability;
    }

    /**
     * @return The maximum litter size for this species.
     */
    @Override
    public int getMaxLitterSize() {
        return maxLitterSize;
    }

    /**
     * @return The maximum age for this species.
     */
    @Override
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * @return The breeding age for this species.
     */
    @Override
    public int getBreedingAge() {
        return breedingAge;
    }

    /**
     * @return The disease spread probability for this species.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return diseaseSpreadProbability;
    }

    /**
     * @return The death-by-disease probability for this species.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return deathByDiseaseProbability;
    }

    /**
     * Check whether this animal can breed with a nearby compatible mate.
     *
     * @return true if a compatible mate is nearby.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= breedingAge && hasCompatibleMateNearby(speciesClass);
    }

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
