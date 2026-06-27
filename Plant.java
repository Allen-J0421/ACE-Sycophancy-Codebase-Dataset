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
     * Make this plant act - that is: make it do whatever it needs to do.
     * @param newPlants A list to receive newly grown plants.
     */
    @Override
    public void act(List<Actor> newPlants)
    {
        super.act(newPlants);
        if (isAlive()) {
            giveBirth(newPlants);
            findWater();
        }
    }

    /**
     * Increase the age and kill the plant when it exceeds its lifespan.
     */
    @Override
    public void incrementAge()
    {
        super.incrementAge();
        if (getAge() > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Look for water adjacent to the plant's current location.
     * @return Where water was found, or null if it wasn't.
     */
    protected Location findWater()
    {
        return getWaterLevel() <= getWaterSearchThreshold() ? super.findWater() : null;
    }

    /**
     * Check whether or not this plant is to reproduce at this step.
     * New growths will be made into free adjacent locations.
     * @param newPlants A list to return newly grown plants.
     * @return The free adjacent locations for the new plants, or null if reproduction is not possible.
     */
    protected List<Location> giveBirth(List<Actor> newPlants)
    {
        int births = reproduce();
        if (births <= 0) {
            return null;
        }

        List<Location> free = getField().getFreeAdjacentLocations(getLocation());
        if (free.isEmpty()) {
            return null;
        }

        Field field = getField();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = createOffspring(field, loc);
            newPlants.add(young);
        }
        return null;
    }

    /**
     * Determine whether this plant can currently reproduce.
     */
    private boolean canReproduce()
    {
        return getWaterLevel() >= getReproductionWaterThreshold();
    }

    /**
     * Generate the number of offspring produced by this plant.
     */
    private int reproduce()
    {
        if (canReproduce() && rand.nextDouble() <= getReproductionProbability()) {
            return rand.nextInt(getMaxLitterSize()) + 1;
        }
        return 0;
    }

    /**
     * @return The age at which this plant can die of old age.
     */
    protected abstract int getMaxAge();

    /**
     * @return The probability that this plant reproduces when conditions are met.
     */
    protected abstract double getReproductionProbability();

    /**
     * @return The maximum number of offspring this plant can produce at once.
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return The minimum water level required to reproduce.
     */
    protected abstract int getReproductionWaterThreshold();

    /**
     * @return The upper bound used for initial water level generation.
     */
    protected abstract int getStartingWaterLevelUpperBound();

    /**
     * @return The water level at which the plant starts seeking water.
     */
    protected abstract int getWaterSearchThreshold();

    /**
     * Create a new offspring of the current plant species.
     * @param field The field the offspring should occupy.
     * @param location The offspring's location.
     * @return A new instance of the same plant species.
     */
    protected abstract Plant createOffspring(Field field, Location location);
}
