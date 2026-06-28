import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Utility methods for rectangular grid coordinate geometry and validation.
 */
public final class GridUtil
{
    private GridUtil()
    {
        // Utility class.
    }

    /**
     * Check whether a coordinate lies within the provided bounds.
     *
     * @param row The row coordinate.
     * @param col The column coordinate.
     * @param depth The grid depth.
     * @param width The grid width.
     * @return True if the coordinate is inside the grid bounds.
     */
    public static boolean isWithinBounds(int row, int col, int depth, int width)
    {
        return row >= 0 && row < depth && col >= 0 && col < width;
    }

    /**
     * Check whether a location lies within the provided bounds.
     *
     * @param location The location to validate.
     * @param depth The grid depth.
     * @param width The grid width.
     * @return True if the location is inside the grid bounds.
     */
    public static boolean isWithinBounds(Location location, int depth, int width)
    {
        return location != null && isWithinBounds(location.getRow(), location.getCol(), depth, width);
    }

    /**
     * Return the adjacent locations around a cell.
     *
     * @param location The center location.
     * @param depth The grid depth.
     * @param width The grid width.
     * @return A shuffled list of valid adjacent locations.
     */
    public static List<Location> adjacentLocations(Location location, int depth, int width)
    {
        assert location != null : "Null location passed to adjacentLocations";
        List<Location> locations = new LinkedList<>();
        if(location == null) {
            return locations;
        }

        int row = location.getRow();
        int col = location.getCol();
        for(int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int nextRow = row + rowOffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int colOffset = -1; colOffset <= 1; colOffset++) {
                    int nextCol = col + colOffset;
                    if(isWithinBounds(nextRow, nextCol, depth, width) && (rowOffset != 0 || colOffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }

        Collections.shuffle(locations, Randomizer.getRandom());
        return locations;
    }
}
