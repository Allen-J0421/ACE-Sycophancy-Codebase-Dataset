import java.util.List;

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
     * Rain increases a water source's volume.
     */
    public void onRain() {
        setVolume(getVolume() + 10);
    }

    /**
     * Fog increases a water source's volume slightly.
     */
    public void onFog() {
        setVolume(getVolume() + 2);
    }

    /**
     * A heatwave halves a water source's volume.
     */
    public void onHeatwave() {
        setVolume(getVolume() / 2);
    }

    /**
     * A water source leaves the simulation once it has run dry.
     * @return true if this water source is empty.
     */
    public boolean isExpired() {
        return isEmpty();
    }

    /**
     * When a water source's infection runs its course it is cured, since
     * infection does not last forever in water.
     */
    protected void expireInfection() {
        notInfected();
    }

    /**
     * @return Whether this water source still holds water, and so can carry
     *         and spread a disease.
     */
    protected boolean isActive() {
        return !isEmpty();
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
