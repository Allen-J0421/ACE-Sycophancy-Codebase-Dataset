import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single organism.
 *
 * @version 2016.02.29
 */
public class Field
{
    /**
     * Callback for visiting each location in the field.
     */
    @FunctionalInterface
    public interface FieldVisitor
    {
        void visit(Location location, Organism organism);
    }

    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the organisms.
    private Organism[][] field;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Organism[depth][width];
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
     * Place an organism at the given location.
     * If there is already an organism at the location it will
     * be lost.
     * @param organism The organism to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Organism organism, int row, int col)
    {
        place(organism, new Location(row, col));
    }
    
    /**
     * Place an organism at the given location.
     * If there is already an organism at the location it will
     * be lost.
     * @param organism The organism to be placed.
     * @param location Where to place the animal.
     */
    public void place(Organism organism, Location location)
    {
        field[location.getRow()][location.getCol()] = organism;
    }
    
    /**
     * Return the organism at the given location, if any.
     * @param location Where in the field.
     * @return The organism at the given location, or null if there is none.
     */
    public Organism getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the object at the given location if it matches the requested type.
     * @param location Where in the field.
     * @param type The expected type.
     * @return The object at the location cast to the given type, or null.
     */
    public <T> T getObjectAt(Location location, Class<T> type)
    {
        return getObjectAt(location.getRow(), location.getCol(), type);
    }
    
    /**
     * Return the organism at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The organism at the given location, or null if there is none.
     */
    public Organism getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Return the object at the given location if it matches the requested type.
     * @param row The desired row.
     * @param col The desired column.
     * @param type The expected type.
     * @return The object at the location cast to the given type, or null.
     */
    public <T> T getObjectAt(int row, int col, Class<T> type)
    {
        Organism object = getObjectAt(row, col);
        if(type.isInstance(object)) {
            return type.cast(object);
        }
        return null;
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
    public Location freeAdjacentLocation(Location location)
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
    public List<Location> adjacentLocations(Location location, int rcoffset)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1 * rcoffset; roffset <= rcoffset; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1 * rcoffset; coffset <= rcoffset; coffset++) {
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
     * Visit every location in the field.
     */
    public void forEachLocation(FieldVisitor visitor)
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                Location location = new Location(row, col);
                visitor.visit(location, getObjectAt(row, col));
            }
        }
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
