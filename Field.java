import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal and a single plant.
 *
 * @version 26/02/2022
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private GridSpace[][] field;
    // Current populations by concrete organism type.
    private Map<Class<?>, Integer> populationCounts;

    /**
     * Represent a field of the given dimensions.
     * 
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new GridSpace[depth][width];
        populationCounts = new LinkedHashMap<>();
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        populationCounts.clear();
        for(int row = 0; row < depth; row++) 
        {
            for(int col = 0; col < width; col++) 
            {
                field[row][col] = new GridSpace();
            }
        }
    }
    
    /**
     * Empty the object type of a field.
     * 
     * @param objectType Object type to clear
     */
    public void clear(Class<?> objectType)
    {
        for(int row = 0; row < depth; row++) 
        {
            for(int col = 0; col < width; col++) 
            {
                clear(new Location(row, col), objectType);
            }
        }
    }
    
    /**
     * Clear the given location.
     * 
     * @param location Location The location to clear.
     * @param objectType Type of object to clear
     */
    public void clear(Location location, Class<?> objectType)
    {
        GridSpace gridSpace = getGridSpace(location);
        if (gridSpace == null) {
            return;
        }

        Object removedObject = gridSpace.removeObject(objectType);
        if (removedObject != null) {
            decrementPopulationCount(removedObject.getClass());
        }
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * 
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object object, int row, int col)
    {
        place(object, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * 
     * @param object The object to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object object, Location location)
    {
        GridSpace gridSpace = ensureGridSpace(location);
        Object replacedObject = gridSpace.setObject(object);

        if (replacedObject != null) {
            decrementPopulationCount(replacedObject.getClass());
        }
        if (object != null) {
            incrementPopulationCount(object.getClass());
        }
    }
    
    /**
     * Return the animal at the given location, if any.
     * 
     * @param location Where in the field.
     * @param objectType Object type to fetch
     * 
     * @return The animal at the given location, or null if there is none.
     */
    public <T> T getObjectAt(Location location, Class<T> objectType)
    {
        return getObjectAt(location.getRow(), location.getCol(), objectType);
    }
    
    /**
     * Return the animal at the given location, if any.
     * 
     * @param row The desired row.
     * @param col The desired column.
     * 
     * @return The animal at the given location, or null if there is none.
     */
    public <T> T getObjectAt(int row, int col, Class<T> objectType)
    {
        return ensureGridSpace(row, col).getObject(objectType);
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * 
     * @param location The location from which to generate an adjacency.
     * 
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * 
     * @param location Get locations adjacent to this.
     * @param objectType Object type to avoid.
     * 
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location, Class<?> objectType)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        
        for(Location next : adjacent) 
        {
            if(getObjectAt(next, objectType) == null) 
            {
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
     * 
     * @param location The location from which to generate an adjacency.
     * @param objectType type to avoid.
     * 
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location, Class<?> objectType)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location, objectType);
        
        if(free.size() > 0) 
        {
            return free.get(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * 
     * @param location The location from which to generate adjacencies.
     * 
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        
        if(location != null)
        {
            int row = location.getRow();
            int col = location.getCol();
            
            for(int roffset = -1; roffset <= 1; roffset++) 
            {
                int nextRow = row + roffset;
                
                if(nextRow >= 0 && nextRow < depth) 
                {
                    for(int coffset = -1; coffset <= 1; coffset++) 
                    {
                        int nextCol = col + coffset;
                        
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) 
                        {
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
     * 
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * 
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Return a snapshot of the tracked populations by concrete type.
     */
    public Map<Class<?>, Integer> getPopulationCounts()
    {
        return new LinkedHashMap<>(populationCounts);
    }

    /**
     * Return how many concrete species currently have a non-zero population.
     */
    public int getActivePopulationTypeCount()
    {
        return populationCounts.size();
    }

    /**
     * Get the grid space at a location without forcing allocation.
     */
    private GridSpace getGridSpace(Location location)
    {
        return field[location.getRow()][location.getCol()];
    }

    /**
     * Get or create the grid space at a location.
     */
    private GridSpace ensureGridSpace(Location location)
    {
        return ensureGridSpace(location.getRow(), location.getCol());
    }

    /**
     * Get or create the grid space at the given coordinates.
     */
    private GridSpace ensureGridSpace(int row, int col)
    {
        if (field[row][col] == null)
        {
            field[row][col] = new GridSpace();
        }

        return field[row][col];
    }

    /**
     * Increase the tracked population for a concrete type.
     */
    private void incrementPopulationCount(Class<?> objectType)
    {
        Integer count = populationCounts.get(objectType);
        if (count == null) {
            populationCounts.put(objectType, 1);
        }
        else {
            populationCounts.put(objectType, count + 1);
        }
    }

    /**
     * Decrease the tracked population for a concrete type.
     */
    private void decrementPopulationCount(Class<?> objectType)
    {
        Integer count = populationCounts.get(objectType);
        if (count == null) {
            return;
        }

        if (count <= 1) {
            populationCounts.remove(objectType);
        }
        else {
            populationCounts.put(objectType, count - 1);
        }
    }
}
