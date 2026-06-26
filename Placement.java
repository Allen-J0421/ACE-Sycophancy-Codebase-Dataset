import java.util.List;

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
        occupy(organism, field, location);
        return new Placement(field, location);
    }

    /**
     * Place an organism at a field location.
     *
     * @param organism The organism to place.
     * @param field The field to place the organism in.
     * @param location The location to occupy.
     */
    public static void occupy(Organism organism, Field field, Location location) {
        field.place(organism, location);
    }

    /**
     * Clear a field location.
     *
     * @param field The field to clear.
     * @param location The location to clear.
     */
    public static void clear(Field field, Location location) {
        field.clear(location);
    }

    /**
     * Clear all occupied locations in a field.
     *
     * @param field The field to clear.
     */
    public static void clearAll(Field field) {
        field.clear();
    }

    /**
     * Return the organism occupying a field location.
     *
     * @param field The field to inspect.
     * @param location The location to inspect.
     * @return The occupying organism, or null if empty.
     */
    public static Organism getOccupant(Field field, Location location) {
        return field.getOrganismAt(location);
    }

    /**
     * Return the organism occupying a field coordinate.
     *
     * @param field The field to inspect.
     * @param row The row to inspect.
     * @param col The column to inspect.
     * @return The occupying organism, or null if empty.
     */
    public static Organism getOccupant(Field field, int row, int col) {
        return field.getOrganismAt(row, col);
    }

    /**
     * Check whether a field location is occupied.
     *
     * @param field The field to inspect.
     * @param location The location to inspect.
     * @return true if an organism occupies the location.
     */
    public static boolean isOccupied(Field field, Location location) {
        return getOccupant(field, location) != null;
    }

    /**
     * Find a free adjacent location.
     *
     * @param field The field to inspect.
     * @param location The origin location.
     * @return A free adjacent location, or null if none is available.
     */
    public static Location freeAdjacentLocation(Field field, Location location) {
        return field.freeAdjacentLocation(location);
    }

    /**
     * Return free adjacent locations.
     *
     * @param field The field to inspect.
     * @param location The origin location.
     * @return A list of free adjacent locations.
     */
    public static List<Location> getFreeAdjacentLocations(Field field, Location location) {
        return field.getFreeAdjacentLocations(location);
    }

    /**
     * Move an organism from its current location to a new location.
     *
     * @param organism The organism to move.
     * @param newLocation The new location to occupy.
     */
    public void move(Organism organism, Location newLocation) {
        if (location != null) {
            clear(field, location);
        }
        location = newLocation;
        occupy(organism, field, newLocation);
    }

    /**
     * Clear the occupied location and detach from the field.
     */
    public void clear() {
        if (location != null) {
            clear(field, location);
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
