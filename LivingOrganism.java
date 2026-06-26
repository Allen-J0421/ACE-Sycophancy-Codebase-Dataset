import java.util.Random;

/**
 * A class representing the shared characteristics between Living Organisms.
 *
 * @version 26/02/2022
 */
public abstract class LivingOrganism
{
    // Whether the organism is alive or not.
    protected boolean alive;
    // The field the organism is stored in.
    protected Field field;
    // The organism's position in the field.
    protected Location location;
    // The food value of itself - how much the hunter's food level increases when eating it.
    protected int foodValue;
    
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();
    
    /**
     * Creates a new Organism at the location.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected LivingOrganism(Field field, Location location) 
    {
        alive = true;
        this.field = field;
        this.location = null;
    }
    
    /**
     * Increments the organism's age.
     */
    protected abstract void incrementAge();
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected abstract void setDead();
    
    /**
     * When called, the organism gets eaten and returns the
     * food value of which the organism eating it increases
     * 
     * @return The food value of the organism being eaten.
     */
    protected abstract int beEaten();
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the organism at the new location in the given field.
     * 
     * @param newLocation The organism's new location.
     */
    protected abstract void setLocation(Location newLocation);
    
    /**
     * Return the organism's field.
     * 
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
}


