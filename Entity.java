import java.util.List;

/**
 * The common base class for every entity in the simulation (animals and plants).
 * Centralises the lifecycle state that Animal and Plant previously each carried
 * their own copy of: alive/dead status, field reference, and grid position.
 */
public abstract class Entity
{
    // Whether the entity is alive or not.
    private boolean alive;
    // The field in which this entity lives.
    private Field<Entity> field;
    // The entity's position in the field.
    private Location location;

    /**
     * Create a new entity at the given location in the given field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Entity(Field<Entity> field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Perform one step of activity. Any entities spawned during this step
     * (e.g. animal offspring) should be appended to newEntities.
     * Plants that do not spawn anything may simply ignore that parameter.
     *
     * @param newEntities A list to receive any entities spawned this step.
     * @param step The current simulation step number.
     * @param weather The current weather.
     */
    public abstract void act(List<Entity> newEntities, int step, String weather);

    /**
     * @return true if the entity is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that this entity is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * @return the entity's current location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Move this entity to a new location in the field.
     * @param newLocation The entity's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * @return the field in which this entity lives.
     */
    protected Field<Entity> getField()
    {
        return field;
    }
}
