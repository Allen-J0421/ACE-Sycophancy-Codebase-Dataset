import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Provides spatial navigation utilities over a {@link Field}.
 *
 * <p>Separates grid-traversal algorithms from the data structure that stores
 * entities. {@link Field} remains a pure container (place/clear/get); all
 * adjacency queries and free-cell searches live here.
 *
 * <p>A single {@code FieldNavigator} is created per simulation field and
 * exposed to entities through {@link Entity#getNavigator()}.
 */
public class FieldNavigator
{
    private static final Random rand = Randomizer.getRandom();

    private final Field<Entity> field;

    /**
     * Create a navigator bound to the given field.
     * @param field The field whose cells will be navigated.
     */
    public FieldNavigator(Field<Entity> field)
    {
        this.field = field;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one within
     * the given radius. The list does not include the location itself.
     * All returned locations lie within the grid bounds.
     * @param location The centre location.
     * @param radius The neighbourhood radius (1 = immediate neighbours).
     * @return A shuffled list of in-bounds adjacent locations.
     */
    public List<Location> adjacentLocations(Location location, int radius)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if (location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for (int roffset = -radius; roffset <= radius; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < field.getDepth()) {
                    for (int coffset = -radius; coffset <= radius; coffset++) {
                        int nextCol = col + coffset;
                        if (nextCol >= 0 && nextCol < field.getWidth()
                                && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Get a shuffled list of the free (unoccupied) adjacent locations.
     * @param location The location whose neighbours are examined.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        for (Location next : adjacentLocations(location, 1)) {
            if (field.getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return the first free location adjacent to the given one,
     * or {@code null} if all neighbours are occupied.
     * @param location The location from which to search.
     * @return A free adjacent location, or {@code null}.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.size() > 0 ? free.get(0) : null;
    }

    /**
     * Return a random location adjacent to the given one.
     * @param location The location from which to generate an adjacency.
     * @return A random adjacent location within the grid.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return adjacentLocations(location, 1).get(0);
    }
}
