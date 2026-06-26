import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Actor
{
    protected static final Random rand = Randomizer.getRandom();

    private int stageOfGrowth;

    /**
     * Returns this species' immutable configuration constants.
     * Each concrete subclass provides a static PlantStats instance.
     */
    protected abstract PlantStats getStats();

    /**
     * Create a new plant at location in field.
     * Stage of growth is initialised randomly within the species' range.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
        this.stageOfGrowth = rand.nextInt(getStats().getNumberOfStages());
    }

    /** Returns the plant's current stage of growth. */
    protected int getStageOfGrowth()
    {
        return stageOfGrowth;
    }

    /**
     * Advances the plant to the next stage of growth if it has not yet reached its maximum.
     * @return true if the plant grew; false if it was already at max stage.
     */
    public boolean incrementGrowth()
    {
        if (stageOfGrowth < getStats().getNumberOfStages()) {
            stageOfGrowth++;
            return true;
        }
        return false;
    }
}
