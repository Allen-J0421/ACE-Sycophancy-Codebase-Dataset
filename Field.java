import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single GridOccupant.
 *
 * @version 2016.02.29
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Storage for grid occupants (animals and plants).
    private GridOccupant[][] grid;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
        grid = new GridOccupant[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear() {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location) {
        grid[location.row()][location.col()] = null;
    }

    /**
     * Place an occupant at the given location.
     * If there is already an occupant at the location it will be lost.
     * @param occupant The occupant to be placed.
     * @param location Where to place the occupant.
     */
    public void place(GridOccupant occupant, Location location)
    {
        grid[location.row()][location.col()] = occupant;
    }

    /**
     * Return the occupant at the given location, if any.
     * @param location Where in the field.
     * @return The occupant at the given location, or null if there is none.
     */
    public GridOccupant getObjectAt(Location location)
    {
        return grid[location.row()][location.col()];
    }

    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location) {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location) {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location) {
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
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location) {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
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
