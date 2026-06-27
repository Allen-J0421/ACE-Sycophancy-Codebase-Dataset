import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.02.28
 */
public abstract class Plant extends Organism
{
    // Shared random source for plant lifecycle decisions.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant at location in field.
     * @param randomAge If true, the plant will have a random age assigned to it.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        setAge(0);
        setWaterLevel(rand.nextInt(getStartingWaterLevelUpperBound()));
        if(randomAge) {
            setAge(rand.nextInt(getMaxAge()));
        }
    }

    /**
     * Run the plant-specific part of the lifecycle after shared upkeep.
     *
     * @param context Shared lifecycle state for the current step.
     */
    @Override
    protected void performLifecycle(SimulationContext context)
    {
        giveBirth(context.getSpawnedActors());
        if (isAlive()) {
            findWater();
        }
    }

    /**
     * Find free adjacent locations for births if this plant can breed.
     */
    @Override
    protected List<Location> getBirthLocations()
    {
        if (canBreed()) {
            return getField().getFreeAdjacentLocations(getLocation());
        }
        return null;
    }

    /**
     * Determine whether this plant currently meets the breeding condition.
     */
    @Override
    protected boolean meetsBreedingCondition()
    {
        return getWaterLevel() >= getBreedingWaterThreshold();
    }

    /**
     * Generate the number of offspring produced by this plant.
     */
    @Override
    protected int breed()
    {
        return breed(getBreedingProbability(), getMaxLitterSize());
    }

    /**
     * @return The probability that this plant reproduces when conditions are met.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum number of offspring this plant can produce at once.
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return The minimum water level required to reproduce.
     */
    protected abstract int getBreedingWaterThreshold();

    /**
     * @return The upper bound used for initial water level generation.
     */
    protected abstract int getStartingWaterLevelUpperBound();

    /**
     * Create a new offspring of the current plant species.
     * @param field The field the offspring should occupy.
     * @param location The offspring's location.
     * @return A new instance of the same plant species.
     */
    protected abstract Organism createOffspring(Field field, Location location);

    @Override
    protected int getWaterSearchThreshold()
    {
        return 5;
    }

    /**
     * Plants do not currently copy additional state to offspring.
     * @param offspring The offspring to initialize.
     */
    @Override
    protected void initializeOffspring(Organism offspring)
    {
        // Default plant offspring state is already complete.
    }
}
