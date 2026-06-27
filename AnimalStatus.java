import java.util.Random;

/**
 * Holds mutable state that is specific to animal behaviour.
 */
public class AnimalStatus
{
    private int foodLevel;
    private final boolean female;
    private boolean nocturnal;
    private boolean sleeping;

    /**
     * Create a new set of animal status values.
     */
    public AnimalStatus(boolean randomAge)
    {
        Random rand = new Random();
        female = rand.nextBoolean();
        nocturnal = false;
        sleeping = false;
        if(!randomAge) {
            foodLevel = rand.nextInt(10) + 8;
        }
    }

    /**
     * Reduce the food level by one step.
     */
    public void decrementFoodLevel()
    {
        foodLevel--;
    }

    /**
     * @return The animal's current food level.
     */
    public int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * @param foodLevel The animal's new food level.
     */
    public void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }

    /**
     * @return Whether the animal is female.
     */
    public boolean isFemale()
    {
        return female;
    }

    /**
     * Mark the animal as nocturnal.
     */
    public void setNocturnal()
    {
        nocturnal = true;
    }

    /**
     * @return Whether the animal is nocturnal.
     */
    public boolean isNocturnal()
    {
        return nocturnal;
    }

    /**
     * Toggle the sleeping state.
     */
    public void toggleSleeping()
    {
        sleeping = !sleeping;
    }

    /**
     * Wake the animal.
     */
    public void wakeUp()
    {
        sleeping = false;
    }

    /**
     * @return Whether the animal is sleeping.
     */
    public boolean isSleeping()
    {
        return sleeping;
    }
}
