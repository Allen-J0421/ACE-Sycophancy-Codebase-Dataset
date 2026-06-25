package field;

/**
 * Represents a location on the Field
 *
 */
public class Location {
    private int row;
    private int col;

    /**
     * Constructor for Location
     * @param row the row of the location
     * @param col the column of the location
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { 
        return row; 
    }

    public int getCol() { 
        return col; 
    }

    /**
     * Check whether this object is equal to another object.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) 
            return false;

        Location location = (Location)obj;
        return location.getRow() == row && location.getCol() == col;
    }

}
