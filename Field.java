import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single living being.
 *
 * @version 2016.02.29
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    private final int depth;
    private final int width;
    // Storage for living beings.
    private final LivingBeing[][] occupants;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        occupants = new LivingBeing[depth][width];
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                occupants[row][col] = null;
            }
        }
    }
    
    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        occupants[location.getRow()][location.getCol()] = null;
    }
    
    /**
     * Place a living being at the given location.
     * If there is already a living being at the location it will
     * be lost.
     * @param being The living being to be placed.
     * @param location Where to place the living being.
     */
    public void place(LivingBeing being, Location location)
    {
        occupants[location.getRow()][location.getCol()] = being;
    }
    
    /**
     * Return the living being at the given location, if any.
     * @param location Where in the field.
     * @return The living being at the given location, or null if there is none.
     */
    public LivingBeing getLivingBeingAt(Location location)
    {
        return getLivingBeingAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the living being at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The living being at the given location, or null if there is none.
     */
    public LivingBeing getLivingBeingAt(int row, int col)
    {
        return occupants[row][col];
    }

    /**
     * Check whether the given location is empty.
     * @param location The location to check.
     * @return true if the location has no living being.
     */
    public boolean isEmpty(Location location)
    {
        return getLivingBeingAt(location) == null;
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            if(isEmpty(next)) {
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
    public Location getFreeAdjacentLocation(Location location)
    {
        // The available free ones.
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
    public List<Location> getAdjacentLocations(Location location)
    {
        assert location != null : "Null location passed to getAdjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
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
