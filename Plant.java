import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Actor
{
    // Growth behaviour component — initialised by each subclass constructor.
    protected GrowthComponent growth;

    protected static final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }

    /** Returns the number of simulation steps between each growth stage advance. */
    public int STEPS_PER_STAGE() { return growth.getStepsPerStage(); }

    /**
     * Checks if the plant is still able to grow and advances the stage if so.
     * @return true if the plant grew; false if already at full maturity.
     */
    public boolean incrementGrowth()
    {
        return growth.increment();
    }
}
