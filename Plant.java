import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Actor
{
    // the stages of growth for  plant
    protected int NUMBER_OF_STAGES;
    protected int STEPS_PER_STAGE = 1;
    protected int STAGE_OF_GROWTH;


    // Implementing abstract methods to return fields to be used by the superclass
    public int STEPS_PER_STAGE(){ return STEPS_PER_STAGE; }
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

    /**
     * Checks if the plant is still able to growth
     * Returns true and increments the growth of the plant 
     * if the plant hasn't reached its final stage.
     * Returns false otherwise
     */
    public boolean incrementGrowth() {
        if(STAGE_OF_GROWTH < NUMBER_OF_STAGES){
            STAGE_OF_GROWTH++;
            return true;
        }
        return false;
    }
    
    
}
