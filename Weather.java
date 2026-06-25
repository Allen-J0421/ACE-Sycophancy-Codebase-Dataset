/**
 * The current weather of the simulation.
 * Every weather type has a multiplier on a plant's growth rate.
 *
 * @version 2022.03.02
 */
public abstract class Weather
{
    //Multiplier on an plant's growth based on the current weather type
    protected float breedingMultiplier;
    
    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        //
    }
    
    /**
     * Return breeding multiplier
     * @Return breedingMultiplier The multiplier on a plant's growth
     */
    protected float getMultiplier(){
        return breedingMultiplier;
    }
}
