
/**
 * A fixed-size two-dimensional grid that maps (row, col) coordinates to objects.
 * Raw storage only — no adjacency or navigation logic.
 *
 * @param <T> The type of object stored in each cell.
 */
public class Grid<T>
{
    private final int depth;
    private final int width;
    private final Object[][] cells;

    /**
     * Create a grid of the given dimensions, initially empty.
     *
     * @param depth Number of rows.
     * @param width Number of columns.
     */
    public Grid(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        cells = new Object[depth][width];
    }

    /** Null out every cell. */
    public void clear()
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                cells[row][col] = null;
            }
        }
    }

    /** Null out a single cell. */
    public void clear(int row, int col)
    {
        cells[row][col] = null;
    }

    /** Store an object at the given coordinates. */
    public void place(T obj, int row, int col)
    {
        cells[row][col] = obj;
    }

    /** Return the object at the given coordinates, or null if the cell is empty. */
    @SuppressWarnings("unchecked")
    public T get(int row, int col)
    {
        return (T) cells[row][col];
    }

    /** Return the number of rows. */
    public int getDepth()
    {
        return depth;
    }

    /** Return the number of columns. */
    public int getWidth()
    {
        return width;
    }
}
