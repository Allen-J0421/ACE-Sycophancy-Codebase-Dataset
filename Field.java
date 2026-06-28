import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
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
     * The list will INCLUDE the location itself.
     * All locations will lie within the grid.
     * 
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocationsIncludingSelf(Location location, int adjacentDistance)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -adjacentDistance; roffset <= adjacentDistance; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -adjacentDistance; coffset <= adjacentDistance; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width) {
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
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location, int adjacentDistance)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -adjacentDistance; roffset <= adjacentDistance; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -adjacentDistance; coffset <= adjacentDistance; coffset++) {
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
    /**
     * Generate a random location from the field.
     * Using randomWidth and randomDepth to create an instance of Location.
     * @return Location a random location.
     */
     public Location generateRandomLocation()
    {
       int randomWidth = rand.nextInt(width);
       int randomDepth = rand.nextInt(depth);
       
       return new Location(randomWidth, randomDepth);
         
    }
    /**
     * Create a list and holds all objects that is adjacent to this specific location.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance used to Controls the range to which this method can search in the field.
     * @return a list of adjacent objects.
     */
    public List<Object> getAllObjectAt(Location location, int adjacentDistance)
    {
        List<Object> adjacentObjectList = new ArrayList<>();
        
        List<Location> adjacent = adjacentLocationsIncludingSelf(location, adjacentDistance);
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
