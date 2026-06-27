/**
 * Base class for anything that occupies a single cell of a Field.
 *
 * Centralises the field + location bookkeeping - placing, moving and
 * clearing - that was previously duplicated, verbatim, by both Organism
 * and WaterSources.
 *
 * @version 2022.03.01
 */
public abstract class FieldOccupant
{
    // The field this occupant lives in.
    private Field field;
    // This occupant's position within the field.
    private Location location;

    /**
     * Place a new occupant into the given field at the given location.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected FieldOccupant(Field field, Location location)
    {
        this.field = field;
        setLocation(location);
    }

    /**
     * Return this occupant's field.
     * @return The field currently occupied.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Return this occupant's location.
     * @return The current location, or null if it has been removed from the field.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the occupant at a new location in its field, clearing the
     * cell it previously occupied.
     * @param newLocation The occupant's new location.
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
     * Remove this occupant from the field: clear its cell and drop the
     * field/location references so it no longer participates in the grid.
     * @return true if it was on the field and has now been removed;
     *         false if it was already absent.
     */
    protected boolean clearFromField()
    {
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
            return true;
        }
        return false;
    }
}
