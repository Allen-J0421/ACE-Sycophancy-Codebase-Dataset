import java.util.ArrayList;
import java.util.List;
/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * Delegates all 2D array management and location-based operations to GridManager.
 *
 * @version 2016.02.29
 */
public class Field
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private final GridManager gridManager;

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
        gridManager = new GridManager(depth, width);
    }

    /*///////////////////////////////////////////////////////////////
                            ANIMAL FIELD LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Empty the field.
     */
    public void clear()
    {
        gridManager.clearAll();
    }

    /**
     * Clear the given location.
     *
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        gridManager.clear(location);
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
        gridManager.place(animal, row, col);
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
        gridManager.place(animal, location);
    }

    /**
     * Return the animal at the given location, if any.
     *
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return gridManager.getObjectAt(location);
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
        return gridManager.getObjectAt(row, col);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     *
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return gridManager.getFreeAdjacentLocations(location);
    }

    /**
     * Groups animals that are contained within the same 20 by 20 block within the main animal field.
     *
     * @return List of animals contained within the same block.
     */
    public List<List<Animal>> getAnimalsPerBlock()
    {
        List<List<Animal>> blocks = new ArrayList<>();
        int width = gridManager.getWidth();
        int depth = gridManager.getDepth();
        for(int i = 0; i < width; i += 20) {
            for(int j = 0; j < depth; j += 20) {
                List<Animal> animals = new ArrayList<>();
                for(int k = i; k < i + 20; k++) {
                    for(int l = j; l < j + 20; l++) {
                        Object obj = gridManager.getObjectAt(l, k);
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
     * Clear the given location in the plant terrain.
     *
     * @param location The location to clear.
     */
    public void clearPlant(Location location)
    {
        gridManager.clearPlant(location);
    }

    /**
     * Place a plant at the given location.
     *
     * @param plant The plant to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void placePlant(Plant plant, int row, int col)
    {
        gridManager.placePlant(plant, row, col);
    }

    /**
     * Place a plant at the given location.
     *
     * @param plant    The plant to be placed.
     * @param location Location to place the plant at.
     */
    public void placePlant(Plant plant, Location location)
    {
        gridManager.placePlant(plant, location);
    }

    /**
     * Return the plant at the given location, if any.
     *
     * @param location The desired location.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(Location location)
    {
        return gridManager.getPlantAt(location);
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
        return gridManager.getPlantAt(row, col);
    }

    /**
     * Get a shuffled list of free adjacent locations in the plant terrain.
     *
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent terrain locations.
     */
    public List<Location> getFreeAdjacentTerrain(Location location)
    {
        return gridManager.getFreeAdjacentTerrain(location);
    }

    /*///////////////////////////////////////////////////////////////
                            GRID AGNOSTIC LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Generate a random location that is adjacent to the given location.
     * The returned location will be within the valid bounds of the field.
     *
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return gridManager.randomAdjacentLocation(location);
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * If there is none, return null.
     *
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area, or null.
     */
    public Location freeAdjacentLocation(Location location)
    {
        return gridManager.freeAdjacentLocation(location);
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
        return gridManager.adjacentLocations(location);
    }

    /**
     * Return the depth of the field.
     *
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return gridManager.getDepth();
    }

    /**
     * Return the width of the field.
     *
     * @return The width of the field.
     */
    public int getWidth()
    {
        return gridManager.getWidth();
    }
}
