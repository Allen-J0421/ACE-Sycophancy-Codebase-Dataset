
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Computes shuffled adjacency lists for positions in a Grid, and provides
 * helpers for locating free (empty) adjacent cells.
 */
public class AdjacencyNavigator
{
    private final Grid<?> grid;
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a navigator backed by the given grid.
     *
     * @param grid The grid whose cells will be inspected for occupancy.
     */
    public AdjacencyNavigator(Grid<?> grid)
    {
        this.grid = grid;
    }

    /**
     * Return a shuffled list of all valid locations adjacent to the given one.
     * The location itself is not included.
     *
     * @param location The centre location.
     * @return A shuffled list of neighbouring locations within grid bounds.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        int row = location.getRow();
        int col = location.getCol();
        List<Location> locations = IntStream.rangeClosed(-1, 1)
            .boxed()
            .flatMap(dr -> IntStream.rangeClosed(-1, 1)
                .filter(dc -> (dr != 0 || dc != 0)
                           && row + dr >= 0 && row + dr < grid.getDepth()
                           && col + dc >= 0 && col + dc < grid.getWidth())
                .mapToObj(dc -> new Location(row + dr, col + dc)))
            .collect(Collectors.toCollection(LinkedList::new));
        Collections.shuffle(locations, rand);
        return locations;
    }

    /**
     * Return a list of free (null) adjacent locations in shuffled order.
     *
     * @param location The centre location.
     * @return A list of unoccupied neighbouring locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        for (Location next : adjacentLocations(location)) {
            if (grid.get(next.getRow(), next.getCol()) == null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return the first free adjacent location, or null if none exists.
     *
     * @param location The centre location.
     * @return A free neighbouring location, or null.
     */
    public Location freeAdjacentLocation(Location location)
    {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.size() > 0 ? free.get(0) : null;
    }

    /**
     * Return a random adjacent location (first element of the shuffled list).
     *
     * @param location The centre location.
     * @return A random neighbouring location within grid bounds.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return adjacentLocations(location).get(0);
    }
}
