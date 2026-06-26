import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single creature.
 *
 * @version 2022/03/02
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Storage for the creatures.
    private Object[][] field;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
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
     * If there is already a creature at the location it will be lost.
     * @param creature The creature to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object creature, int row, int col)
    {
        field[row][col] = creature;
    }

    /**
     * Place a creature at the given location.
     * If there is already a creature at the location it will be lost.
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
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new ArrayList<>();
        List<Location> adjacent = adjacentLocations(location, 1);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location adjacent to the given location.
     * @param location The location from which to generate an adjacency.
     * @return A valid free location, or null if none exists.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.isEmpty() ? null : free.get(0);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one (excluding self).
     * All locations lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance The search radius.
     * @return A shuffled list of adjacent locations.
     */
    public List<Location> adjacentLocations(Location location, int adjacentDistance)
    {
        return buildAdjacentLocations(location, adjacentDistance, false);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one, including the location itself.
     * All locations lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance The search radius.
     * @return A shuffled list of adjacent locations including the origin.
     */
    public List<Location> adjacentLocationsIncludingSelf(Location location, int adjacentDistance)
    {
        return buildAdjacentLocations(location, adjacentDistance, true);
    }

    /**
     * Return the depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Return the width of the field.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Generate a random location within the field.
     * @return A random Location with valid row and column coordinates.
     */
    public Location generateRandomLocation()
    {
        int randomRow = rand.nextInt(depth);
        int randomCol = rand.nextInt(width);
        return new Location(randomRow, randomCol);
    }

    /**
     * Return a list of all non-null objects within adjacentDistance of location (inclusive).
     * @param location The centre of the search area.
     * @param adjacentDistance The search radius.
     * @return A list of objects found in the search area.
     */
    public List<Object> getAllObjectAt(Location location, int adjacentDistance)
    {
        List<Object> adjacentObjectList = new ArrayList<>();
        for(Location loc : adjacentLocationsIncludingSelf(location, adjacentDistance)) {
            Object adjacentObject = getObjectAt(loc);
            if(adjacentObject != null) {
                adjacentObjectList.add(adjacentObject);
            }
        }
        return adjacentObjectList;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /**
     * Build a shuffled list of grid locations within adjacentDistance of the given location.
     * @param location  The origin.
     * @param distance  The search radius.
     * @param includeSelf  Whether to include the origin itself.
     * @return A shuffled list of matching locations within the field bounds.
     */
    private List<Location> buildAdjacentLocations(Location location, int distance, boolean includeSelf)
    {
        List<Location> locations = new ArrayList<>();
        int row = location.getRow();
        int col = location.getCol();
        for(int roffset = -distance; roffset <= distance; roffset++) {
            int nextRow = row + roffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int coffset = -distance; coffset <= distance; coffset++) {
                    int nextCol = col + coffset;
                    if(nextCol >= 0 && nextCol < width
                            && (includeSelf || roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }
        Collections.shuffle(locations, rand);
        return locations;
    }
}
