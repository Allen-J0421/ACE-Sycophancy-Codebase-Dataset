/**
 * Shared lifecycle and field-position state for all organisms in the
 * simulation.
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;

    /**
     * Create a new organism at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected final boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected final void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected final Location getLocation()
    {
        return location;
    }

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected final void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected final Field getField()
    {
        return field;
    }
}
