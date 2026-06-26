import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 *
 * @version 2022.03.02
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;

    private Set<Location> emptyLocs = new HashSet<>();

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
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
        field[location.getRow()][location.getCol()] = animal;
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
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
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Returns a random set of patches from getFreePatches() based on a given rate
     * getFreePatches() returns a list of size n, only (n*rate) random locations are returned
     * @param rateOfGeneration The rate
     */
    public List<Location> getRandomFreePatches(double rateOfGeneration)
    {
        List<Location> patches = getFreePatches();
        List<Location> randPatches = new ArrayList<>();
        for(int i=0; i<rateOfGeneration* patches.size(); i++)
        {
            Location selectedPatch = patches.get(rand.nextInt(patches.size()));
            randPatches.add(selectedPatch);
            for(Location loc : adjacentLocations(selectedPatch)){
                // avoids duplicates in the patches
                patches.remove(loc);
            }
        }
        return randPatches;
    }

    /**
     * Gets the free patches in the field and returns them.
     * A 'patch' is a 3x3 square of locations, the locations in the list are the centers of said squares
     * Patches do not overlap, so the adjacent locations of one patch don't have common locations with
     * the adjacent locations of any other square
     *
     * @return The list of free patches in the field.
     */
    public List<Location> getFreePatches()
    {
        List<Location> res = new ArrayList<>();
        for(int i=0; i<field.length; i++){
            for(int j=0; j<field[1].length; j++){
                if (field[i][j] == null){
                    List<Location> nearby = adjacentLocations(new Location(i, j));
                    boolean allNulls = true;
                    for(Location loc : nearby){
                        if(getObjectAt(loc) != null){
                            allNulls = false;
                        }
                    }
                    if(allNulls){
                        res.add(new Location(i, j));
                    }
                }
            }
        }

        return res;
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            Object objectAtNext = getObjectAt(next);
            if(objectAtNext == null) {
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
     * Returns adjacent locations in the field containing a certain class
     * For example, adjacentSpecies(location, Grass.class) returns the adjacent locations with Grass
     * @param location The location of an organism.
     * @param speciesClass The type of species.
     * @return adjacentSpecies A list of adjacent locations with the specified species 
     */
    public List<Location> adjacentLocationsWithSpecies(Location location, Class<?> speciesClass) {
        List<Location> free = adjacentLocations(location);
        List<Location> res = new ArrayList<>();
        for (Location loc : free) {
            if (getObjectAt(loc) != null && getObjectAt(loc).getClass()==speciesClass) {
                res.add(loc);
            }
        }
        return res;
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
