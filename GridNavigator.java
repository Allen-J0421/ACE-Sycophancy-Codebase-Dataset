import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Encapsulates coordinate-based grid traversal for a rectangular grid,
 * decoupled from any storage or occupancy implementation.
 *
 * @version 2022.03.02
 */
public class GridNavigator {

    private static final Random rand = Randomizer.getRandom();

    private final int depth;
    private final int width;

    /**
     * @param depth The number of rows in the grid.
     * @param width The number of columns in the grid.
     */
    public GridNavigator(int depth, int width) {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     *
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location) {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if (location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for (int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < depth) {
                    for (int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the origin itself.
                        if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            // Shuffle the list. Several other methods rely on the list being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Get a shuffled list of adjacent locations that satisfy the given free-cell predicate.
     *
     * @param location   The location whose neighbours are examined.
     * @param isOccupied Returns true for any location that is already occupied.
     * @return A list of adjacent locations for which isOccupied returns false.
     */
    public List<Location> getFreeAdjacentLocations(Location location, Predicate<Location> isOccupied) {
        List<Location> free = new LinkedList<>();
        for (Location next : adjacentLocations(location)) {
            if (!isOccupied.test(next)) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Generate a random location adjacent to the given one, within the valid grid bounds.
     * The shuffled list produced by adjacentLocations means get(0) is already random.
     *
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location) {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
}
