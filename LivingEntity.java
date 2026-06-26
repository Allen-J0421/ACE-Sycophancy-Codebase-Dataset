/**
 * Shared state and lifecycle for objects that occupy a field location.
 */
public abstract class LivingEntity
{
    private boolean alive;
    private Field field;
    private Location location;

    /**
     * Create a new entity at a location in a field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected LivingEntity(Field field, Location location) {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Check whether the entity is alive.
     * @return true if the entity is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the entity is no longer alive.
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
     * Return the entity's location.
     * @return The entity's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the entity at the new location in the given field.
     * @param newLocation The entity's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the entity's field.
     * @return The entity's field.
     */
    protected Field getField()
    {
        return field;
    }
}
