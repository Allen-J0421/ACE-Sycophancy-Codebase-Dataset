import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.02.28
 */
public abstract class Plant extends Organism
{
    // Shared random number generator
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
        if(!randomAge) {
            setAge(0);
            setWaterLevel(rand.nextInt(15));
        }
    }

    // --- Abstract methods each subclass must define ---

    /** @return The maximum age before this plant dies. */
    abstract protected int getMaxAge();

    /** @return The probability of reproducing when conditions are met. */
    abstract protected double getReproductionProbability();

    /** @return The maximum number of offspring per reproduction event. */
    abstract protected int getMaxLitterSize();

    /** @return True if this plant is currently able to reproduce. */
    abstract protected boolean canReproduce();

    /** @return A new instance of this plant type placed at loc. */
    abstract protected Plant createOffspring(Field field, Location loc);

    /**
     * Increase the age. This could result in the plant's death.
     */
    @Override
    public void incrementAge() {
        super.incrementAge();
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Attempt to reproduce into free adjacent locations.
     * @param newPlants A list to return newly grown plants.
     */
    protected void giveBirth(List<Actor> newPlants)
    {
        int births = reproduce();
        if(births == 0) return;

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for(int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            newPlants.add(createOffspring(field, loc));
        }
    }

    /**
     * Generate a number representing the number of new growths, if it can reproduce.
     * @return The number of new growths (may be zero).
     */
    private int reproduce()
    {
        int births = 0;
        if(canReproduce() && rand.nextDouble() <= getReproductionProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Look for water adjacent to the plant's current location.
     * @return Where water was found, or null if it wasn't.
     */
    protected Location findWater() {
        if(getWaterLevel() <= 5) {
            super.findWater();
        }
        return null;
    }
}
