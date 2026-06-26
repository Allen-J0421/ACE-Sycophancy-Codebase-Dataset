/**
 * Tracks and updates an organism's placement in a field.
 */
public class Placement {

    private Field field;
    private Location location;

    private Placement(Field field, Location location) {
        this.field = field;
        this.location = location;
    }

    /**
     * Place an organism in a field.
     *
     * @param organism The organism to place.
     * @param field The field to place the organism in.
     * @param location The location to occupy.
     * @return A placement tracking the occupied field location.
     */
    public static Placement place(Organism organism, Field field, Location location) {
        field.place(organism, location);
        return new Placement(field, location);
    }

    /**
     * Move an organism from its current location to a new location.
     *
     * @param organism The organism to move.
     * @param newLocation The new location to occupy.
     */
    public void move(Organism organism, Location newLocation) {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(organism, newLocation);
    }

    /**
     * Clear the occupied location and detach from the field.
     */
    public void clear() {
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the occupied field.
     *
     * @return The occupied field, or null if detached.
     */
    public Field getField() {
        return field;
    }

    /**
     * Return the occupied location.
     *
     * @return The occupied location, or null if detached.
     */
    public Location getLocation() {
        return location;
    }
}
