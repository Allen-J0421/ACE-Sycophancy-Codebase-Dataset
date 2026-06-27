import java.util.List;

/**
 * Abstract class Creature - A class representing shared characteristics of creatures(animals and plants.)
 *
 * @version 2022/03/02
 */
public abstract class Creature
{
    // Whether the creature is alive or not.
    private boolean alive;
    // The creature's field.
    private Field field;
    // The creature's position in the field.
    private Location location;
    
    
    /**
     * Create a new creature at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Creature(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this creature act - that is: make it do
     * whatever it wants/needs to do.
     * @param newCreatures A list to receive newly born creatures.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     */
    abstract public double act(List<Creature> newCreatures, SimulationContext context);

    /**
     * Check whether the creature is alive or not.
     * @return true if the creature is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
      /**
     * Indicate that the creature is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the creature's location.
     * @return The creature's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the creature at the new location in the given field.
     * @param newLocation The creature's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
     /**
     * Return the creature's field.
     * @return The creature's field.
     */
    protected Field getField()
    {
        return field;
    }
    

}
