/**
 * Component that tracks an entity's field and location.
 */
public final class SpatialComponent implements EntityComponent
{
    private Field field;
    private Location location;

    public SpatialComponent(Field field, Location location)
    {
        this.field = field;
        this.location = location;
    }

    public Field getField()
    {
        return field;
    }

    public Location getLocation()
    {
        return location;
    }

    public void place(Object occupant)
    {
        ensureBound();
        field.place(occupant, location);
    }

    public void move(Object occupant, Location newLocation)
    {
        ensureBound();
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(occupant, newLocation);
    }

    public void clear()
    {
        if(field != null && location != null) {
            field.clear(location);
        }
        field = null;
        location = null;
    }

    private void ensureBound()
    {
        if(field == null) {
            throw new IllegalStateException("Spatial component is no longer bound to a field.");
        }
    }
}
