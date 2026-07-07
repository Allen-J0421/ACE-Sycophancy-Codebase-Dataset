import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represent a rectangular grid of field positions.
 * Cell storage is delegated to {@link Grid}; this class owns the
 * spatial-query logic (adjacency, shuffling, free-location search).
 *
 * @version 26/02/2022
 */
public class Field
{
    private static final SimRandom rand = Randomizer.getRandom();

    private final Grid grid;

    /**
     * Represent a field of the given dimensions.
     *
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        grid = new Grid(depth, width);
    }

    /** Empty every cell in the field. */
    public void clear()
    {
        grid.clear();
    }

    /**
     * Clear the slot of the given type from every cell.
     *
     * @param objectType The class whose slot to clear.
     */
    public void clear(Class objectType)
    {
        grid.clear(objectType);
    }

    /**
     * Clear the slot of the given type at the specified location.
     *
     * @param location   The cell to modify.
     * @param objectType The class whose slot to clear.
     */
    public void clear(Location location, Class objectType)
    {
        grid.clear(location, objectType);
    }

    /**
     * Place an object at the given row and column.
     *
     * @param object The object to place.
     * @param row    Row coordinate.
     * @param col    Column coordinate.
     */
    public void place(Object object, int row, int col)
    {
        grid.place(object, new Location(row, col));
    }

    /**
     * Place an object at the given location.
     *
     * @param object   The object to place.
     * @param location Where to place it.
     */
    public void place(Object object, Location location)
    {
        grid.place(object, location);
    }

    /**
     * Return the object of the requested type at the given location, or null.
     *
     * @param location   Where in the field.
     * @param objectType The class of the object to retrieve.
     * @return The stored object, or null if the slot is empty.
     */
    public Object getObjectAt(Location location, Class objectType)
    {
        return grid.getObjectAt(location, objectType);
    }

    /**
     * Return the object of the requested type at the given row/column, or null.
     *
     * @param row        The desired row.
     * @param col        The desired column.
     * @param objectType The class of the object to retrieve.
     * @return The stored object, or null if the slot is empty.
     */
    public Object getObjectAt(int row, int col, Class objectType)
    {
        return grid.getObjectAt(row, col, objectType);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     *
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();

        if (location != null)
        {
            int row = location.getRow();
            int col = location.getCol();

            for (int roffset = -1; roffset <= 1; roffset++)
            {
                int nextRow = row + roffset;

                if (nextRow >= 0 && nextRow < grid.getDepth())
                {
                    for (int coffset = -1; coffset <= 1; coffset++)
                    {
                        int nextCol = col + coffset;

                        if (nextCol >= 0 && nextCol < grid.getWidth() && (roffset != 0 || coffset != 0))
                        {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            Collections.shuffle(locations, rand.asRandom());
        }

        return locations;
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     *
     * @param location   Get locations adjacent to this.
     * @param objectType Object type to treat as occupying a cell.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location, Class objectType)
    {
        List<Location> free = new LinkedList<>();

        for (Location next : adjacentLocations(location))
        {
            if (getObjectAt(next, objectType) == null)
            {
                free.add(next);
            }
        }

        return free;
    }

    /**
     * Try to find a free location adjacent to the given location.
     *
     * @param location   The location from which to search.
     * @param objectType Type to treat as occupying a cell.
     * @return A free adjacent location, or null if none exists.
     */
    public Location freeAdjacentLocation(Location location, Class objectType)
    {
        List<Location> free = getFreeAdjacentLocations(location, objectType);
        return free.size() > 0 ? free.get(0) : null;
    }

    /**
     * Generate a random location adjacent to the given one.
     *
     * @param location The location from which to generate an adjacency.
     * @return A valid adjacent location within the grid.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return adjacentLocations(location).get(0);
    }

    /** @return The depth of the field. */
    public int getDepth() { return grid.getDepth(); }

    /** @return The width of the field. */
    public int getWidth() { return grid.getWidth(); }
}
