/**
 * Represent a rectangular grid of field positions.
 * Each position stores at most one entity.
 *
 * <p>This class is a pure data container: it supports placing, clearing, and
 * retrieving entities by location. Spatial navigation utilities (adjacency
 * queries, free-cell search) are provided by {@link FieldNavigator}.
 *
 * <p>The type parameter {@code E} constrains what may be stored so that
 * {@link #getObjectAt} returns a typed value rather than a raw {@code Object},
 * eliminating the need for callers to cast. The backing store is an
 * {@code Object[][]} array (Java prohibits generic array creation), but the
 * single {@code @SuppressWarnings} cast inside {@link #getObjectAt} is sound
 * because {@link #place} is the only write path and it accepts only {@code E}.
 *
 * @param <E> The type of entity stored in this field (must extend {@link Entity}).
 * @version 2016.02.29
 */
public class Field<E extends Entity>
{
    // The depth and width of the field.
    private int depth, width;
    // Backing grid — typed externally as E, but stored as Object to work around
    // Java's prohibition on generic array creation.
    private Object[][] grid;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        grid = new Object[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        grid[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an entity at the given location.
     * If there is already an entity at the location it will be lost.
     * @param entity The entity to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(E entity, int row, int col)
    {
        place(entity, new Location(row, col));
    }

    /**
     * Place an entity at the given location.
     * If there is already an entity at the location it will be lost.
     * @param entity The entity to be placed.
     * @param location Where to place the entity.
     */
    public void place(E entity, Location location)
    {
        grid[location.getRow()][location.getCol()] = entity;
    }

    /**
     * Return the entity at the given location, if any.
     * @param location Where in the field.
     * @return The entity at the given location, or null if there is none.
     */
    public E getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the entity at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The entity at the given location, or null if there is none.
     */
    @SuppressWarnings("unchecked")
    public E getObjectAt(int row, int col)
    {
        return (E) grid[row][col];
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
