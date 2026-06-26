import java.util.LinkedList;
import java.util.List;

/**
 * Represent a location in a rectangular grid.
 *
 * @version 2016.02.29
 */
public class Location
{
    // Row and column positions.
    private int row;
    private int col;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Implement content equality.
     */
    public boolean equals(Object obj) {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }
    
    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString()
    {
        return row + "," + col;
    }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (row << 16) + col;
    }
    
    /**
     * @return The row.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * @return The column.
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Return all valid adjacent locations in a bounded grid.
     * The returned list does not include this location.
     * @param depth The maximum number of rows in the grid.
     * @param width The maximum number of columns in the grid.
     * @return The valid adjacent locations.
     */
    public List<Location> adjacentLocations(int depth, int width) {
        List<Location> locations = new LinkedList<>();
        for(int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for(int colOffset = -1; colOffset <= 1; colOffset++) {
                Location next = translate(rowOffset, colOffset);
                if(!next.equals(this) && next.isWithin(depth, width)) {
                    locations.add(next);
                }
            }
        }
        return locations;
    }

    /**
     * Return a location translated by the given offsets.
     */
    private Location translate(int rowOffset, int colOffset) {
        return new Location(row + rowOffset, col + colOffset);
    }

    /**
     * Check whether this location is inside a bounded grid.
     */
    private boolean isWithin(int depth, int width) {
        return row >= 0 && row < depth && col >= 0 && col < width;
    }
}
