/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Actor
{
    // the stages of growth for  plant
    protected int numberOfStages;
    protected int stepsPerStage = 1;
    protected int stageOfGrowth;

    /**
     * Create a new plant at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(SimulationContext context, Field field, Location location)
    {
        super(context, field, location);
    }

    public int getStepsPerStage()
    {
        return stepsPerStage;
    }

    /**
     * Checks if the plant is still able to growth
     * Returns true and increments the growth of the plant 
     * if the plant hasn't reached its final stage.
     * Returns false otherwise
     */
    public boolean incrementGrowth() {
        if(stageOfGrowth < numberOfStages){
            stageOfGrowth++;
            return true;
        }
        return false;
    }
    
    
}
