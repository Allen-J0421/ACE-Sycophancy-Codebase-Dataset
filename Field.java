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
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private final int depth;
    private final int width;
    // Storage for organisms — typed to avoid unchecked casts at call sites.
    private final Organism[][] field;

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
     * If there is already an organism at the location it will be lost.
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
     * If there is already an organism at the location it will be lost.
     * @param organism The organism to be placed.
     * @param location Where to place the organism.
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
     * Generate a random location that is adjacent to the given location.
     * The returned location will be within the valid bounds of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
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
        for(Location next : adjacentLocations(location)) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * @param location The location from which to generate an adjacency.
     * @return A valid free location, or null if none exists.
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
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
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
     */
    public int getDepth() { return depth; }

    /**
     * Return the width of the field.
     */
    public int getWidth() { return width; }
}
