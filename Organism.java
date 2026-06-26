/**
 * This class represents shared characteristics of organisms
 * in the simulation. 
 *
 * @version 2022.03.2
 */
public abstract class Organism {
    

    // Whether the organism is alive or not.
    private boolean alive;
    private final RandomProvider randomProvider;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;


    protected abstract int getFoodValue();

    /**
     * Creates a new organism. 
     */
    public Organism(RandomProvider randomProvider, Field field, Location location)
    {
        alive = true;
        this.randomProvider = randomProvider;
        this.field = field;
        setLocation(location);
    }

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
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
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }

    protected RandomProvider getRandomProvider()
    {
        return randomProvider;
    }

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
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

}
