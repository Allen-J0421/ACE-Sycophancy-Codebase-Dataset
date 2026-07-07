import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
/**
 * Manages the two underlying 2D grids (actor field and plant terrain) and provides
 * all location-based operations: placement, retrieval, clearing, and adjacency queries.
 *
 * @version 1.0
 */
public class GridManager
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private static final Random rand = Randomizer.getRandom();
    private final int depth;
    private final int width;
    private final Object[][] field;
    private final Plant[][] terrain;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructs a GridManager for a field of the given dimensions.
     *
     * @param depth The number of rows.
     * @param width The number of columns.
     */
    public GridManager(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        this.field   = new Object[depth][width];
        this.terrain = new Plant[depth][width];
    }

    /*///////////////////////////////////////////////////////////////
                         FULL-GRID OPERATIONS
    //////////////////////////////////////////////////////////////*/

    /**
     * Clears every cell in both the actor field and the plant terrain.
     */
    public void clearAll()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col]   = null;
                terrain[row][col] = null;
            }
        }
    }

    /*///////////////////////////////////////////////////////////////
                         ACTOR GRID OPERATIONS
    //////////////////////////////////////////////////////////////*/

    /**
     * Clears the given location in the actor grid.
     *
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Places an actor at the given location (row, col form).
     *
     * @param actor The actor to place.
     * @param row   Row coordinate.
     * @param col   Column coordinate.
     */
    public void place(Object actor, int row, int col)
    {
        place(actor, new Location(row, col));
    }

    /**
     * Places an actor at the given location.
     *
     * @param actor    The actor to place.
     * @param location Where to place the actor.
     */
    public void place(Object actor, Location location)
    {
        field[location.getRow()][location.getCol()] = actor;
    }

    /**
     * Returns the actor at the given location.
     *
     * @param location The desired location.
     * @return The actor at that location, or null.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Returns the actor at the given (row, col) coordinates.
     *
     * @param row The desired row.
     * @param col The desired column.
     * @return The actor at that location, or null.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Returns a shuffled list of free locations adjacent to the given one in the actor grid.
     *
     * @param location The origin location.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return getFreeAdjacent(field, location);
    }

    /**
     * Returns the first free location adjacent to the given one in the actor grid,
     * or null if all adjacent cells are occupied.
     *
     * @param location The origin location.
     * @return A free adjacent location, or null.
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

    /*///////////////////////////////////////////////////////////////
                       PLANT TERRAIN OPERATIONS
    //////////////////////////////////////////////////////////////*/

    /**
     * Clears the given location in the plant terrain.
     *
     * @param location The location to clear.
     */
    public void clearPlant(Location location)
    {
        terrain[location.getRow()][location.getCol()] = null;
    }

    /**
     * Places a plant at the given location (row, col form).
     *
     * @param plant The plant to place.
     * @param row   Row coordinate.
     * @param col   Column coordinate.
     */
    public void placePlant(Plant plant, int row, int col)
    {
        placePlant(plant, new Location(row, col));
    }

    /**
     * Places a plant at the given location.
     *
     * @param plant    The plant to place.
     * @param location Where to place the plant.
     */
    public void placePlant(Plant plant, Location location)
    {
        terrain[location.getRow()][location.getCol()] = plant;
    }

    /**
     * Returns the plant at the given location.
     *
     * @param location The desired location.
     * @return The plant at that location, or null.
     */
    public Plant getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }

    /**
     * Returns the plant at the given (row, col) coordinates.
     *
     * @param row The desired row.
     * @param col The desired column.
     * @return The plant at that location, or null.
     */
    public Plant getPlantAt(int row, int col)
    {
        return terrain[row][col];
    }

    /**
     * Returns a shuffled list of free locations adjacent to the given one in the plant terrain.
     *
     * @param location The origin location.
     * @return A list of free adjacent terrain locations.
     */
    public List<Location> getFreeAdjacentTerrain(Location location)
    {
        return getFreeAdjacent(terrain, location);
    }

    /*///////////////////////////////////////////////////////////////
                       LOCATION GEOMETRY
    //////////////////////////////////////////////////////////////*/

    /**
     * Returns a random location adjacent to the given one (first element of a shuffled list).
     *
     * @param location The origin location.
     * @return A valid adjacent location.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Returns a shuffled list of all valid locations adjacent to the given one.
     * The original location is not included.
     *
     * @param location The location from which to generate adjacencies.
     * @return A shuffled list of adjacent locations within grid bounds.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
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

    /*///////////////////////////////////////////////////////////////
                              ACCESSORS
    //////////////////////////////////////////////////////////////*/

    /**
     * Returns the depth (number of rows) of the grid.
     *
     * @return The depth.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Returns the width (number of columns) of the grid.
     *
     * @return The width.
     */
    public int getWidth()
    {
        return width;
    }

    /*///////////////////////////////////////////////////////////////
                           PRIVATE HELPERS
    //////////////////////////////////////////////////////////////*/

    /**
     * Returns the free adjacent locations within the given grid at the given location.
     *
     * @param grid     A 2D storage grid to check for occupancy.
     * @param location The origin location.
     * @return A list of free adjacent locations.
     */
    private List<Location> getFreeAdjacent(Object[][] grid, Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location loc : adjacent) {
            if(grid[loc.getRow()][loc.getCol()] == null) {
                free.add(loc);
            }
        }
        return free;
    }
}
