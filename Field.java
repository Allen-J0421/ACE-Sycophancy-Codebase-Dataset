import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position stores at most one entity.
 *
 * <p>The type parameter {@code E} constrains what may be stored so that
 * {@link #getObjectAt} returns a typed value rather than a raw {@code Object},
 * eliminating the need for callers to cast. The backing store is an
 * {@code Object[][]} array (Java prohibits generic array creation), but
 * the single {@code @SuppressWarnings} cast inside {@link #getObjectAt} is
 * sound because {@link #place} is the only write path and it accepts only {@code E}.
 *
 * @param <E> The type of entity stored in this field (must extend {@link Entity}).
 * @version 2016.02.29
 */
public class Field<E extends Entity>
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Backing grid — typed externally as E, but stored as Object to work around
    // Java's prohibition on generic array creation.
    private Object[][] grid;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        grid = new Object[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        grid[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an entity at the given location.
     * If there is already an entity at the location it will be lost.
     * @param entity The entity to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(E entity, int row, int col)
    {
        place(entity, new Location(row, col));
    }

    /**
     * Place an entity at the given location.
     * If there is already an entity at the location it will be lost.
     * @param entity The entity to be placed.
     * @param location Where to place the entity.
     */
    public void place(E entity, Location location)
    {
        grid[location.getRow()][location.getCol()] = entity;
    }

    /**
     * Return the entity at the given location, if any.
     * @param location Where in the field.
     * @return The entity at the given location, or null if there is none.
     */
    public E getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the entity at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The entity at the given location, or null if there is none.
     */
    @SuppressWarnings("unchecked")
    public E getObjectAt(int row, int col)
    {
        return (E) grid[row][col];
    }

    /**
     * Generate a random location that is adjacent to the given location,
     * or is the same location. The returned location will be within the
     * valid bounds of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location, 1);
        return adjacent.get(0);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location, 1);
        for (Location next : adjacent) {
            if (getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * If there is none, return null.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area, or null if all adjacent
     *         locations are occupied.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        if (free.size() > 0) {
            return free.get(0);
        } else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param rcoffset The radius of the neighbourhood.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location, int rcoffset)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if (location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for (int roffset = -rcoffset; roffset <= rcoffset; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < depth) {
                    for (int coffset = -rcoffset; coffset <= rcoffset; coffset++) {
                        int nextCol = col + coffset;
                        if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            // Shuffle the list. Several other methods rely on the list being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
