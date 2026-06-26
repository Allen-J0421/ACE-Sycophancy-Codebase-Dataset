/**
 * Represent a location in a rectangular grid.
 *
 * @version 2016.02.29
 */
public class Location
{
    private final int row;
    private final int col;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.row && col == other.col;
        }
        return false;
    }

    @Override
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
    @Override
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
}
