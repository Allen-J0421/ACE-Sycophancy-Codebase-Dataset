import java.awt.Color;
import java.util.List;

/**
 * The common base class for every entity in the simulation (animals and plants).
 * Centralises the lifecycle state that Animal and Plant previously each carried
 * their own copy of: alive/dead status, field reference, and grid position.
 */
public abstract class Entity implements Viewable
{
    // Whether the entity is alive or not.
    private boolean alive;
    // The field in which this entity lives.
    private Field<Entity> field;
    // Spatial navigation utilities over that field.
    private FieldNavigator navigator;
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
        this.navigator = new FieldNavigator(field);
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
            navigator = null;
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

    /**
     * @return the navigator for spatial queries over the field.
     */
    protected FieldNavigator getNavigator()
    {
        return navigator;
    }

    // --- Viewable ---

    /**
     * @return the color used to render this entity in the simulation view.
     */
    @Override
    public abstract Color getDisplayColor();

    /**
     * @return a short human-readable name for this entity type.
     * Defaults to the simple class name (e.g. "Cat"); override for a custom label.
     */
    @Override
    public String getDisplayName()
    {
        return getClass().getSimpleName();
    }

    /**
     * Whether this entity type counts toward the viability check.
     * Implemented by {@link Animal} (true) and {@link Plant} (false).
     */
    @Override
    public abstract boolean countsTowardViability();
}
