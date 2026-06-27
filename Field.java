import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single object.
 *
 * @version 2022.02.28
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    // The depth and width of the field.
    private int depth, width;
    // Storage for all the objects in the simulation
    private Object[][] field;

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
     * Clear the given location. Equivalent to placing nothing there, so the
     * cell-coordinate arithmetic stays in one place ({@link #place}).
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        place(null, location);
    }

    /**
     * Place an object at the given location.
     * If there is already a object at the location it will
     * be lost.
     * @param object The object to be placed.
     * @param location Where to place the object.
     */
    public void place(Object object, Location location)
    {
        field[location.getRow()][location.getCol()] = object;
    }
    
    /**
     * Return the object at the given location, if any.
     * @param location Where in the field.
     * @return The object at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the object at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The object at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }
    
    /**
     * A shuffled stream of the locations adjacent to the given one.
     * Backed by {@link #adjacentLocations(Location)}, so it consumes
     * randomness identically.
     * @param location The location to scan around.
     * @return A stream of the adjacent locations, in random order.
     */
    public Stream<Location> adjacentLocationStream(Location location)
    {
        return adjacentLocations(location).stream();
    }

    /**
     * The adjacent locations whose occupant satisfies the given test, in the
     * field's random adjacency order. The test receives each cell's occupant,
     * which is null for an empty cell.
     * @param location The location to scan around.
     * @param occupantTest The test applied to each adjacent cell's occupant.
     * @return The matching adjacent locations.
     */
    public List<Location> adjacentLocationsMatching(Location location, Predicate<Object> occupantTest)
    {
        return adjacentLocationStream(location)
            .filter(where -> occupantTest.test(getObjectAt(where)))
            .collect(Collectors.toList());
    }

    /**
     * The first adjacent location whose occupant satisfies the given test, or
     * null if no adjacent occupant matches.
     * @param location The location to scan around.
     * @param occupantTest The test applied to each adjacent cell's occupant.
     * @return A matching adjacent location, or null.
     */
    public Location firstAdjacentLocationMatching(Location location, Predicate<Object> occupantTest)
    {
        return adjacentLocationStream(location)
            .filter(where -> occupantTest.test(getObjectAt(where)))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        return adjacentLocationsMatching(location, Objects::isNull);
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
        return firstAdjacentLocationMatching(location, Objects::isNull);
    }

    /**
     * Whether the given (row, column) coordinate lies within this field's
     * bounds. This is the single home for the grid's coordinate-bounds
     * arithmetic.
     * @param row The row coordinate.
     * @param col The column coordinate.
     * @return true if the coordinate is inside the grid.
     */
    public boolean isInBounds(int row, int col)
    {
        return row >= 0 && row < depth && col >= 0 && col < width;
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
                for(int coffset = -1; coffset <= 1; coffset++) {
                    // Exclude the original location.
                    if(roffset != 0 || coffset != 0) {
                        int nextRow = row + roffset;
                        int nextCol = col + coffset;
                        // Exclude locations outside the grid.
                        if(isInBounds(nextRow, nextCol)) {
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
