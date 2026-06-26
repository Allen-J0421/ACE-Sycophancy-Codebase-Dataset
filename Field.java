import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    private final int depth;
    private final int width;
    // Storage for the creatures.
    private final Object[][] field;

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
        for(Object[] row : field) {
            Arrays.fill(row, null);
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
     * @param location Where in the field.
     * @return The creature at the given location, or null if there is none.
     */
    public Creature getCreatureAt(Location location)
    {
        Object object = getObjectAt(location);
        if(object instanceof Creature) {
            return (Creature) object;
        }
        return null;
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
        List<Location> free = new ArrayList<>();
        for(Location next : adjacentLocations(location, 1)) {
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
        List<Location> free = getFreeAdjacentLocations(location);
        return free.isEmpty() ? null : free.get(0);
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
        return adjacentLocations(location, adjacentDistance, true);
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
        return adjacentLocations(location, adjacentDistance, false);
    }

    /**
     * Build a shuffled list of locations around the given one.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance Controls the range to search.
     * @param includeSelf Whether to include the provided location.
     * @return A shuffled list of valid locations.
     */
    private List<Location> adjacentLocations(Location location, int adjacentDistance, boolean includeSelf)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new ArrayList<>();
        if(location == null || adjacentDistance < 0) {
            return locations;
        }

        int rowStart = Math.max(0, location.getRow() - adjacentDistance);
        int rowEnd = Math.min(depth - 1, location.getRow() + adjacentDistance);
        int colStart = Math.max(0, location.getCol() - adjacentDistance);
        int colEnd = Math.min(width - 1, location.getCol() + adjacentDistance);

        for(int row = rowStart; row <= rowEnd; row++) {
            for(int col = colStart; col <= colEnd; col++) {
                if(includeSelf || row != location.getRow() || col != location.getCol()) {
                    locations.add(new Location(row, col));
                }
            }
        }

        // Shuffle the list. Several other methods rely on the list
        // being in a random order.
        Collections.shuffle(locations, rand);
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
     * Use a random row and column to create an instance of Location.
     * @return Location a random location.
     */
    public Location generateRandomLocation()
    {
       int row = rand.nextInt(depth);
       int col = rand.nextInt(width);

       return new Location(row, col);
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

        for(Location where : adjacentLocationsIncludingSelf(location, adjacentDistance)) {
            Object adjacentObject = getObjectAt(where);
            if(adjacentObject != null) {
                adjacentObjectList.add(adjacentObject);
            }
        }
        return adjacentObjectList;
    }
}
