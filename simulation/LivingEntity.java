package simulation;

/**
 * Shared state and lifecycle for objects that occupy a field location.
 */
public abstract class LivingEntity
{
    private boolean alive;
    private SimulationContext context;
    private Location location;

    /**
     * Create a new entity at a location in a field.
     *
     * @param context The simulation context currently occupied.
     * @param location The location within the field.
     */
    protected LivingEntity(SimulationContext context, Location location) {
        alive = true;
        this.context = context;
        this.location = location;
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
        if(location != null && context != null) {
            context.emit(new DeathEvent(this, this, location));
        }
        location = null;
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
        Location oldLocation = location;
        location = newLocation;
        if(context != null) {
            context.emit(new MovementEvent(this, this, oldLocation, newLocation));
        }
    }

    /**
     * Return the simulation context.
     * @return The entity's simulation context.
     */
    protected SimulationContext getContext()
    {
        return context;
    }
}
