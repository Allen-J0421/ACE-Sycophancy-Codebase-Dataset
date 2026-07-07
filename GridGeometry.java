import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Encapsulates coordinate and adjacency calculations for a bounded rectangular
 * grid, independent of what is stored at each position.
 *
 * @version 2022/03/02
 */
public class GridGeometry
{
    private static final Random rand = Randomizer.getRandom();

    private final int depth;
    private final int width;

    /**
     * Create geometry for a grid of the given dimensions.
     * @param depth The number of rows.
     * @param width The number of columns.
     */
    public GridGeometry(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    public int getDepth() { return depth; }
    public int getWidth() { return width; }

    /**
     * Generate a random location within the grid bounds.
     * @return A random location.
     */
    public Location generateRandomLocation()
    {
        int randomWidth = rand.nextInt(width);
        int randomDepth = rand.nextInt(depth);
        return new Location(randomWidth, randomDepth);
    }

    /**
     * Generate a random location adjacent to the given one.
     * @param location The source location.
     * @return A randomly chosen adjacent location.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location, 1);
        return adjacent.get(0);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one,
     * excluding the location itself. All locations lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance The Chebyshev distance defining the neighbourhood.
     * @return A shuffled list of valid adjacent locations.
     */
    public List<Location> adjacentLocations(Location location, int adjacentDistance)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -adjacentDistance; roffset <= adjacentDistance; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -adjacentDistance; coffset <= adjacentDistance; coffset++) {
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

    /**
     * Return a shuffled list of locations adjacent to the given one,
     * including the location itself. All locations lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param adjacentDistance The Chebyshev distance defining the neighbourhood.
     * @return A shuffled list of valid locations.
     */
    public List<Location> adjacentLocationsIncludingSelf(Location location, int adjacentDistance)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -adjacentDistance; roffset <= adjacentDistance; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -adjacentDistance; coffset <= adjacentDistance; coffset++) {
                        int nextCol = col + coffset;
                        if(nextCol >= 0 && nextCol < width) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            Collections.shuffle(locations, rand);
        }
        return locations;
    }
}
