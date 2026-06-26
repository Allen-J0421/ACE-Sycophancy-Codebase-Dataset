/**
 * Represent a location in a rectangular grid.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Location
{
    // Row and column positions:
    private final int row;
    private final int col;

    /**
     * Represent a row and column.
     * 
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Implement content equality.
     * 
     * @param object The object this object is being compared to.
     */
    @Override
    public boolean equals(Object object)
    {
        // Check that the other object is also of type Location:
        if (object instanceof Location)
        {
            Location other = (Location) object;
            
            // Chcek that the other object has the same row and column:
            return row == other.getRow() && col == other.getCol();
        }
        else
        {
            return false;
        }
    }
    
    /**
     * @return A string representation of the location in the form row, column.
     */
    @Override
    public String toString() { return row + "," + col; }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * 
     * @return A hashcode for the location.
     */
    @Override
    public int hashCode() { return (row << 16) + col; }
    
    /**
     * @return The row.
     */
    public int getRow() { return row; }
    
    /**
     * @return The column.
     */
    public int getCol() { return col; }
}
