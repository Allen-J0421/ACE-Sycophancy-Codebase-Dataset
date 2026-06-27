import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    /**
     * Return adjacent locations around this organism.
     */
    protected final List<Location> adjacentLocations(int radius)
    {
        return field.adjacentLocations(location, radius);
    }

    /**
     * Return free adjacent locations around this organism.
     */
    protected final List<Location> freeAdjacentLocations()
    {
        return field.getFreeAdjacentLocations(location);
    }

    /**
     * Find a free adjacent location around this organism.
     */
    protected final Location freeAdjacentLocation()
    {
        return field.freeAdjacentLocation(location);
    }

    /**
     * Return the object at a location if it matches the requested type.
     */
    protected final <T> T getObjectAt(Location targetLocation, Class<T> type)
    {
        return field.getObjectAt(targetLocation, type);
    }

    /**
     * Find the first adjacent location containing a matching target.
     */
    protected final <T> Location findAdjacentLocation(Class<T> targetClass, int radius,
                                                      Predicate<T> predicate)
    {
        for(Location where : adjacentLocations(radius)) {
            T candidate = getObjectAt(where, targetClass);
            if(candidate != null && predicate.test(candidate)) {
                return where;
            }
        }
        return null;
    }

    /**
     * Apply an action to each adjacent matching target.
     */
    protected final <T> void forEachAdjacent(Class<T> targetClass, int radius,
                                             Consumer<T> action)
    {
        for(Location where : adjacentLocations(radius)) {
            T candidate = getObjectAt(where, targetClass);
            if(candidate != null) {
                action.accept(candidate);
            }
        }
    }

    /**
     * Make this organism act for the current step.
     * @param newOrganisms A list to receive newly created organisms.
     * @param step The current simulation step.
     */
    abstract public void act(List<Organism> newOrganisms, SimulationStep step);
}
