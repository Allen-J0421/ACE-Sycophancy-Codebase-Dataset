import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single creature.
 *
 * @version 2022/03/02
 */
public class Field
{
    private final GridGeometry geometry;
    // Storage for the creatures.
    private Object[][] field;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        geometry = new GridGeometry(depth, width);
        field = new Object[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < geometry.getDepth(); row++) {
            for(int col = 0; col < geometry.getWidth(); col++) {
                field[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place a creature at the given location.
     * If there is already a creature at the location it will
     * be lost.
     * @param creature The creature to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object creature, int row, int col)
    {
        place(creature, new Location(row, col));
    }

    /**
     * Place an creature at the given location.
     * If there is already an creature at the location it will
     * be lost.
     * @param creature The creature to be placed.
     * @param location Where to place the creature.
     */
    public void place(Object creature, Location location)
    {
        field[location.getRow()][location.getCol()] = creature;
    }

    /**
     * Return the creature at the given location, if any.
     * @param location Where in the field.
     * @return The creature at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the creature at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The creature at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Generate a random location that is adjacent to the given location.
     * The returned location will be within the valid bounds of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return geometry.randomAdjacentLocation(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = geometry.adjacentLocations(location, 1);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * If there is none, return null.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will INCLUDE the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocationsIncludingSelf(Location location, int adjacentDistance)
    {
        return geometry.adjacentLocationsIncludingSelf(location, adjacentDistance);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location, int adjacentDistance)
    {
        return geometry.adjacentLocations(location, adjacentDistance);
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return geometry.getDepth();
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return geometry.getWidth();
    }

    /**
     * Generate a random location from the field.
     * @return A random location within the grid.
     */
    public Location generateRandomLocation()
    {
        return geometry.generateRandomLocation();
    }

    /**
     * Create a list and holds all objects that is adjacent to this specific location.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance Controls the range to which this method can search in the field.
     * @return A list of adjacent objects.
     */
    public List<Object> getAllObjectAt(Location location, int adjacentDistance)
    {
        List<Object> adjacentObjectList = new ArrayList<>();
        List<Location> adjacent = geometry.adjacentLocationsIncludingSelf(location, adjacentDistance);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object adjacentObject = getObjectAt(where);
            if(adjacentObject != null) {
                adjacentObjectList.add(adjacentObject);
            }
        }
        return adjacentObjectList;
    }
}
