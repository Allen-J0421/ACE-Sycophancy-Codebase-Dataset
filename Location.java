/**
 * Represent a location in a rectangular grid.
 * Equality and hash code are automatically derived from row and col.
 *
 * @version 2016.02.29
 */
public record Location(int row, int col) {
    @Override
    public String toString() {
        return row + "," + col;
    }
}
