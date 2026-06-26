import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 *
 * @version 2016.02.29
 */
public class Field
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    /** Side length of each disease-simulation block. Field dimensions must be multiples of this. */
    public static final int BLOCK_SIZE = 20;

    private static final Random rand = Randomizer.getRandom();
    private final int depth, width;

    /*///////////////////////////////////////////////////////////////
                               ACTOR STORAGE
    //////////////////////////////////////////////////////////////*/

    private final Object[][] field;
    private final Plant[][] terrain;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

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
        terrain = new Plant[depth][width];
    }

    /*///////////////////////////////////////////////////////////////
                            ANIMAL FIELD LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
                terrain[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     *
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location, it will be lost.
     *
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        field[row][col] = animal;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will be lost.
     *
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
        field[location.getRow()][location.getCol()] = animal;
    }

    /**
     * Return the animal at the given location, if any.
     *
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     *
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     *
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return getFreeAdjacent(field, location);
    }

    /**
     * Groups animals that are contained within the same BLOCK_SIZE x BLOCK_SIZE block.
     *
     * @return List of per-block animal lists.
     */
    public List<List<Animal>> getAnimalsPerBlock()
    {
        List<List<Animal>> blocks = new ArrayList<>();
        for(int col = 0; col < width; col += BLOCK_SIZE) {
            for(int row = 0; row < depth; row += BLOCK_SIZE) {
                List<Animal> animals = new ArrayList<>();
                for(int c = col; c < col + BLOCK_SIZE; c++) {
                    for(int r = row; r < row + BLOCK_SIZE; r++) {
                        Object obj = getObjectAt(r, c);
                        if(obj instanceof Animal) {
                            animals.add((Animal) obj);
                        }
                    }
                }
                blocks.add(animals);
            }
        }
        return blocks;
    }

    /*///////////////////////////////////////////////////////////////
                            PLANT TERRAIN LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Clear the given location in the terrain grid.
     *
     * @param location The location to clear.
     */
    public void clearPlant(Location location)
    {
        terrain[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place a plant at the given location.
     * If there is already a plant at the location it will be lost.
     *
     * @param plant The plant to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void placePlant(Plant plant, int row, int col)
    {
        terrain[row][col] = plant;
    }

    /**
     * Place a plant at the given location.
     * If there is already a plant at the location it will be lost.
     *
     * @param plant The plant to be placed.
     * @param location Location to place the plant at.
     */
    public void placePlant(Plant plant, Location location)
    {
        terrain[location.getRow()][location.getCol()] = plant;
    }

    /**
     * Return the plant at the given location, if any.
     *
     * @param location The desired location.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }

    /**
     * Return the plant at the given location, if any.
     *
     * @param row The desired row.
     * @param col The desired column.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(int row, int col)
    {
        return terrain[row][col];
    }

    /**
     * Get a shuffled list of free adjacent terrain locations.
     *
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent terrain locations.
     */
    public List<Location> getFreeAdjacentTerrain(Location location)
    {
        return getFreeAdjacent(terrain, location);
    }

    /*///////////////////////////////////////////////////////////////
                            GRID AGNOSTIC LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Given a grid and a location, returns the free adjacent locations within that grid.
     *
     * @param grid A 2 dimensional storage space to store actors.
     * @param location The location to seek adjacents for.
     */
    private List<Location> getFreeAdjacent(Object[][] grid, Location location)
    {
        List<Location> free = new LinkedList<>();
        for(Location loc : adjacentLocations(location)) {
            if(grid[loc.getRow()][loc.getCol()] == null) {
                free.add(loc);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * If there is none, return null.
     *
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area, or null if none is free.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.isEmpty() ? null : free.get(0);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     *
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
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
}
