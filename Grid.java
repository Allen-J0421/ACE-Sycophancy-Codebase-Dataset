/**
 * Encapsulates the 2D array of {@link GridSpace} cells that forms the
 * simulation's spatial data structure.
 *
 * Responsibilities: cell initialisation, placement, retrieval, and clearing.
 * Higher-level spatial queries (adjacency, shuffling) stay in {@link Field}.
 */
public class Grid
{
    private final int depth;
    private final int width;
    private final GridSpace[][] cells;

    /**
     * Create a grid of the given dimensions. All cells are initialised
     * to empty {@link GridSpace} instances immediately.
     *
     * @param depth Number of rows.
     * @param width Number of columns.
     */
    public Grid(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        cells = new GridSpace[depth][width];
        initialise();
    }

    /** Reset every cell to an empty {@link GridSpace}. */
    public void clear()
    {
        initialise();
    }

    /**
     * Clear the slot of the given type in every cell.
     *
     * @param objectType The class whose slot to clear (Animal or Plant).
     */
    public void clear(Class objectType)
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                cells[row][col].clear(objectType);
            }
        }
    }

    /**
     * Clear the slot of the given type at a specific location.
     *
     * @param location   The cell to modify.
     * @param objectType The class whose slot to clear.
     */
    public void clear(Location location, Class objectType)
    {
        cells[location.getRow()][location.getCol()].clear(objectType);
    }

    /**
     * Place an object into its type-appropriate slot at the given location.
     *
     * @param object   The object to store (must be Animal or Plant).
     * @param location The target cell.
     */
    public void place(Object object, Location location)
    {
        cells[location.getRow()][location.getCol()].setObject(object);
    }

    /**
     * Return the object of the requested type at the given location.
     *
     * @param location   The cell to inspect.
     * @param objectType The class of the object to retrieve.
     * @return The stored object, or null if the slot is empty.
     */
    public Object getObjectAt(Location location, Class objectType)
    {
        return getObjectAt(location.getRow(), location.getCol(), objectType);
    }

    /**
     * Return the object of the requested type at the given row/column.
     *
     * @param row        Row index.
     * @param col        Column index.
     * @param objectType The class of the object to retrieve.
     * @return The stored object, or null if the slot is empty.
     */
    public Object getObjectAt(int row, int col, Class objectType)
    {
        return cells[row][col].getObject(objectType);
    }

    /** @return The number of rows in the grid. */
    public int getDepth() { return depth; }

    /** @return The number of columns in the grid. */
    public int getWidth() { return width; }

    private void initialise()
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                cells[row][col] = new GridSpace();
            }
        }
    }
}
