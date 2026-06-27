import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single object.
 *
 * Positions are keyed by {@link Location}, which has correct equals/hashCode,
 * so the grid can never suffer from integer-index transposition bugs and
 * bounds violations at the storage layer are impossible.
 *
 * @version 2X (modified)
 */
public class Field
{
    private static final Random rand = Randomizer.getRandom();

    private final int depth, width;
    // Only occupied cells are present in the map; absence means empty.
    private final HashMap<Location, Object> grid;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        grid = new HashMap<>(depth * width);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        grid.clear();
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        grid.remove(location);
    }

    /**
     * Place an object at the given location.
     * If there is already an object at the location it will be lost.
     * @param occupant The object to be placed.
     * @param row      Row coordinate of the location.
     * @param col      Column coordinate of the location.
     */
    public void place(Object occupant, int row, int col)
    {
        place(occupant, new Location(row, col));
    }

    /**
     * Place an object at the given location.
     * If there is already an object at the location it will be lost.
     * @param occupant The object to be placed.
     * @param location Where to place the object.
     */
    public void place(Object occupant, Location location)
    {
        grid.put(location, occupant);
    }

    /**
     * Return the object at the given location, if any.
     * @param location Where in the field.
     * @return The object at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return grid.get(location);
    }

    /**
     * Return the object at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The object at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return grid.get(new Location(row, col));
    }

    /**
     * Return a view of all objects currently occupying the field.
     * The collection contains no nulls — absent keys represent empty cells.
     * @return an unmodifiable view of all field occupants
     */
    public Collection<Object> getOccupants()
    {
        return Collections.unmodifiableCollection(grid.values());
    }

    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
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
        for (Location next : adjacentLocations(location)) {
            if (getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Get all items of a particular type near to a location.
     * @param playerType the type
     * @param location   the location
     * @return all players of specified type near specified location
     */
    public List<Object> getItemOfTypeNear(Class playerType, Location location)
    {
        ArrayList<Object> items = new ArrayList<>();
        for (Location loc : adjacentLocations(location)) {
            Object item = getObjectAt(loc);
            if (playerType.isInstance(item)) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.size() > 0 ? free.get(0) : null;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if (location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for (int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < depth) {
                    for (int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
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
