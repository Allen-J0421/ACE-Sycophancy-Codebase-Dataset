/**
 * A living participant in the simulation - an animal or a plant.
 *
 * Captures the state and behaviour common to both: being alive, occupying a
 * location within a field, ageing, and being removed from the field upon
 * death. The only thing that differs between organisms is which grid they
 * live on, which subclasses supply through {@link #placeInField(Location)}
 * and {@link #clearFromField(Location)}.
 *
 * @version 1.0
 */
public abstract class Organism implements Actor
{

    /*///////////////////////////////////////////////////////////////
                                  STATE
    //////////////////////////////////////////////////////////////*/

    protected boolean alive;
    protected Field field;
    protected Location location;
    protected int age;

    /*///////////////////////////////////////////////////////////////
                          SHARED LIFE-CYCLE LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Increments the age, dying once the maximum age is exceeded.
     *
     * @param maxAge The maximum age this organism can reach before dying.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Check whether this organism is alive or not.
     *
     * @return true if this organism is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that this organism is no longer alive.
     *
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            clearFromField(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return this organism's location.
     *
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Return this organism's field.
     *
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Place this organism at the new location in the given field, clearing it
     * from its previous location first.
     *
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            clearFromField(location);
        }
        location = newLocation;
        placeInField(newLocation);
    }

    /*///////////////////////////////////////////////////////////////
                            GRID PLACEMENT HOOKS
    //////////////////////////////////////////////////////////////*/

    /**
     * Place this organism into its grid at the given location.
     *
     * @param location Where to place this organism.
     */
    protected abstract void placeInField(Location location);

    /**
     * Remove this organism from its grid at the given location.
     *
     * @param location The location to clear.
     */
    protected abstract void clearFromField(Location location);
}
