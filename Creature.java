import java.util.List;

/**
 * Abstract class Creature - A class representing shared characteristics of creatures (animals and plants).
 *
 * @version 2022/03/02
 */
public abstract class Creature
{
    private boolean alive;
    private Field field;
    private Location location;

    /**
     * Create a new creature at location in field.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Creature(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Make this creature act for one simulation step.
     * @param newCreatures A list to receive newly born creatures.
     * @param atDayTime    true if the current step is daytime.
     * @param oxygenLevel  The dissolved oxygen level in the water.
     * @param disease      The disease active in the simulation.
     * @param step         The current step number.
     * @return The net oxygen change produced or consumed this step.
     */
    public abstract double act(List<Creature> newCreatures, boolean atDayTime,
                               double oxygenLevel, Disease disease, int step);

    /**
     * Return true if this creature is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Mark this creature as dead and remove it from the field.
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

    /** Return the creature's current location. */
    protected Location getLocation() { return location; }

    /** Return the field this creature occupies. */
    protected Field getField() { return field; }

    /**
     * Place this creature at the given location in its field,
     * clearing the previous location first.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
}
