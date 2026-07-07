import java.util.List;

/**
 * A rectangular field that stores simulation actors at grid positions.
 * Storage is delegated to Grid; adjacency navigation is delegated to AdjacencyNavigator.
 *
 * @version 2016.02.29
 */
public class Field
{
    private final Grid<Object> grid;
    private final AdjacencyNavigator navigator;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        grid = new Grid<>(depth, width);
        navigator = new AdjacencyNavigator(grid);
    }

    /** Empty the field. */
    public void clear()
    {
        grid.clear();
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        grid.clear(location.getRow(), location.getCol());
    }

    /**
     * Place an animal at the given location.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        grid.place(animal, row, col);
    }

    /**
     * Place an animal at the given location.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
        grid.place(animal, location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return grid.get(location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return grid.get(row, col);
    }

    /**
     * Generate a random location that is adjacent to the given location.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        return navigator.randomAdjacentLocation(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return navigator.getFreeAdjacentLocations(location);
    }

    /**
     * Try to find a free location that is adjacent to the given location.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area, or null if none exists.
     */
    public Location freeAdjacentLocation(Location location)
    {
        return navigator.freeAdjacentLocation(location);
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself. All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        return navigator.adjacentLocations(location);
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return grid.getDepth();
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return grid.getWidth();
    }
}
