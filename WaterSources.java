import java.util.List;
import java.util.ArrayList;

/**
 *  A class representing shared characteristics of water sources.
 *
 * @version 2022.02.28
 */
public abstract class WaterSources extends FieldOccupant implements Actor
{
    // Whether the water source contains any water or not.
    private boolean isEmpty;
    // The volume of the water source, increases whenever it rains and decreases when water is drunk or taken from it.
    private int volumeLevel;
    // Whether the water source is infected with a disease or not.
    private boolean infected;
    // A list of what diseases the water is infected by.
    private List diseases;

    /**
     * Create a new water source at location in field.
     * @param randomVolume If true, the water source will have a random volume assigned to it.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public WaterSources(boolean randomVolume, Field field, Location location)
    {
        super(field, location);
        if (!randomVolume) {
            setVolume(10);
        }
        isEmpty = false;
        diseases = new ArrayList<>();
    }

    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive any actors relevant to action
     */
    public void act(List<Actor> actorsList)
    {

    }

    /**
     * Infects the water source with a disease
     * (to spread to animals that drink from it)
     */
    protected void setInfected() {
        infected = true;
    }

    /** 
     * Infection does not last forever in water, removes disease after
     * a certain duration
     */
    protected void notInfected() {
        infected = false;
    }

    /**
     * @return Whether the water source is infected with a disease or not.
     */
    protected boolean isInfected(){
        return infected;
    }

    /** 
     * @return Whether there is water in the water source or not
     */
    public boolean isEmpty() 
    {
        return isEmpty;    
    }

    /**
     * Indicate that the water source is empty
     * It is removed from the field.
     */
    public void setDead()
    {
        if(volumeLevel <= 0 && clearFromField()) {
            isEmpty = true;
        }
    }

    /**
     * Sets the current volume of the waterSource to a new one.
     * @param volumeLevel The new volume of the water source.
     */
    protected void setVolume(int volumeLevel)
    {
        this.volumeLevel = volumeLevel;
    }

    /**
     * @return Current volume of the water source
     */
    protected int getVolume()
    { 
        return volumeLevel;
    }

    /**
     * Reduces the current volume of the water source by the number passed
     * in the parameter. 
     * @param waterValue The value you want to deduct from the volume of 
     *                   the water source.
     */
    public void reduceVolume(int waterValue) {
        //Used when water is drunk by an animal or accessed by a plant. 
        //waterValue would be the water value of the water source.
        volumeLevel = volumeLevel - waterValue;
    }
}
