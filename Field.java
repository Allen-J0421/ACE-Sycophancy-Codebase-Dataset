/**
 * Represent a rectangular grid of field positions.
 * Each position stores a single object. Spatial query operations
 * (adjacency, free patches, etc.) are handled by FieldAnalyzer.
 *
 * @version 2022.03.02
 */
public class Field
{
    // The depth and width of the field.
    private int depth, width;
    // Storage for the objects placed in the field.
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
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an object at the given location.
     * @param animal The object to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }

    /**
     * Place an object at the given location.
     * @param animal The object to be placed.
     * @param location Where to place the object.
     */
    public void place(Object animal, Location location)
    {
        field[location.getRow()][location.getCol()] = animal;
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
     * Returns the raw grid array.
     */
    public Object[][] getField()
    {
        return field;
    }

    /**
     * Return the depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Return the width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
