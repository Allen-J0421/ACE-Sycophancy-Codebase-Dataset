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
    private static final int DISEASE_BLOCK_SIZE = 20;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    // the dimensions for the grids
    private final int depth, width;

    /*///////////////////////////////////////////////////////////////
                               ACTOR STORAGE
    //////////////////////////////////////////////////////////////*/
    
    private final Animal[][] animals;
    private final Plant[][] plants;
    
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
        animals = new Animal[depth][width];
        plants = new Plant[depth][width];
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
                animals[row][col] = null;
                plants[row][col] = null;
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
        animals[location.getRow()][location.getCol()] = null;
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location, it will be lost.
     * 
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void placeAnimal(Animal animal, int row, int col)
    {
        placeAnimal(animal, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will be lost.
     * 
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeAnimal(Animal animal, Location location)
    {
        animals[location.getRow()][location.getCol()] = animal;
    }
    
    /**
     * Return the animal at the given location, if any.
     * 
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return getAnimalAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * 
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(int row, int col)
    {
        return animals[row][col];
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * 
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return getFreeAdjacent(animals, location);
    }
    
    /**
     * Groups animals that are contained within the same 20 by 20 block within the main animal field.
     * 
     * @return List of animals contained within the same block.
     */
    public List<List<Animal>> getAnimalsPerBlock()
    {
        List<List<Animal>> blocks = new ArrayList<>();
        for(int colStart = 0 ; colStart < width; colStart += DISEASE_BLOCK_SIZE){
            for (int rowStart = 0; rowStart < depth; rowStart += DISEASE_BLOCK_SIZE) {
                List<Animal> blockAnimals = new ArrayList<>();
                int colEnd = Math.min(colStart + DISEASE_BLOCK_SIZE, width);
                int rowEnd = Math.min(rowStart + DISEASE_BLOCK_SIZE, depth);
                for (int col = colStart; col < colEnd; col++) {
                    for(int row = rowStart; row < rowEnd; row++) {
                        Animal animal = getAnimalAt(row, col);
                        if(animal != null) {
                            blockAnimals.add(animal);
                        }
                    }
                }
                blocks.add(blockAnimals);
            }
        }
        return blocks;
    }
    
    /*///////////////////////////////////////////////////////////////
                            PLANT TERRAIN LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Clear the given location.
     * 
     * @param location The location to clear.
     */
    
    public void clearPlant(Location location)
    {
        plants[location.getRow()][location.getCol()] = null;
    }
    
    /**
     * Place a plant at the given location.
     * If there is already a plant at the location it will be lost.
     * 
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void placePlant(Plant plant, int row, int col)
    {
        placePlant(plant, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will be lost.
     * 
     * @param animal The animal to be placed.
     * @param location location to place the animal at
     */
    public void placePlant(Plant plant, Location location)
    {
        plants[location.getRow()][location.getCol()] = plant;
    }
    
    /**
     * Return the plant at the given location, if any.
     * 
     * @param the desired location.
     * @return The plant at the given location, or null if there is none.
     */    
    public Plant getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * 
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Plant getPlantAt(int row, int col)
    {
        return plants[row][col];
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * 
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentTerrain(Location location)
    {
        return getFreeAdjacent(plants, location);
    }
    
    /*///////////////////////////////////////////////////////////////
                            GRID AGNOSTIC LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * 
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
    
    /**
     * Given a grid and a location, returns the free adjacent location within that grid.
     * 
     * @param grid A 2 dimensional storage space to store actors.
     * @params location the location to seek the adjacents for.
     */
    private List<Location> getFreeAdjacent(Object[][] grid, Location location) 
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for (Location loc : adjacent) {
            if(grid[loc.getRow()][loc.getCol()] == null) {
                free.add(loc);
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
     * 
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
