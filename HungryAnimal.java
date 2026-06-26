import java.util.List;
/**
 * Shared behavior for animals that track hunger and hunt for food.
 *
 * @version 2022.03.02
 */
public abstract class HungryAnimal extends Animal {

    private final AnimalTraitConfig traits;
    private final TimeOfDay restingTime;
    private int foodLevel;

    /**
     * Constructor for a hungry animal in the simulation.
     *
     * @param foodLevel The food level of this animal.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which this animal resides.
     * @param location The location in which this animal spawns into.
     * @param traits Immutable species configuration.
     */
    public HungryAnimal(int foodLevel, boolean randomAge, Field field, Location location,
                        AnimalTraitConfig traits, TimeOfDay restingTime) {
        super(randomAge, traits.maxAge(), field, location);
        this.traits = traits;
        this.restingTime = restingTime;
        this.foodLevel = foodLevel;
    }

    /**
     * Shared hunting turn for predator and scavenger animals.
     *
     * @param newAnimals Newly born organisms from this turn.
     * @param weather The current simulation weather.
     * @param time The current simulation time.
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

        if (rand.nextDouble() <= traits.deathByDiseaseProbability()) {
            remove();
            return;
        }

        Location newLocation;
        if (rand.nextDouble() <= traits.diseaseSpreadProbability()) {
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
        return traits.breedingProbability();
    }

    /**
     * @return The maximum litter size for this species.
     */
    @Override
    public int getMaxLitterSize() {
        return traits.maxLitterSize();
    }

    /**
     * @return The maximum age for this species.
     */
    @Override
    public int getMaxAge() {
        return traits.maxAge();
    }

    /**
     * @return The breeding age for this species.
     */
    @Override
    public int getBreedingAge() {
        return traits.breedingAge();
    }

    /**
     * @return The disease spread probability for this species.
     */
    @Override
    protected double getDiseaseSpreadProbability() {
        return traits.diseaseSpreadProbability();
    }

    /**
     * @return The death-by-disease probability for this species.
     */
    @Override
    protected double getDeathByDiseaseProbability() {
        return traits.deathByDiseaseProbability();
    }

    /**
     * Check whether this animal can breed with a nearby compatible mate.
     *
     * @return true if a compatible mate is nearby.
     */
    @Override
    public boolean canBreed() {
        return getAge() >= traits.breedingAge() && hasCompatibleMateNearby(traits.speciesClass());
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
