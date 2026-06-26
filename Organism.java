/**
 * A shared base class for living entities that occupy the field.
 *
 * @version 01.03.22
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
    public Organism(Field field, Location location) {
        alive = true;
        this.field = field;
        setLocation(location);
    }

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
    protected void setDead() {
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
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation) {
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
    protected Field getField()
    {
        return field;
    }
}
