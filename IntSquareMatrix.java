public final class IntSquareMatrix {

    private final int[][] values;

    private IntSquareMatrix(int[][] values) {
        validateSquareMatrix(values);
        this.values = copyMatrix(values);
    }

    public static IntSquareMatrix from(int[][] values) {
        return new IntSquareMatrix(values);
    }

    public int size() {
        return values.length;
    }

    public int get(int row, int column) {
        validateIndex(row, values.length);
        validateIndex(column, values.length);
        return values[row][column];
    }

    public int[][] copyValues() {
        return copyMatrix(values);
    }

    private static void validateSquareMatrix(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix must not be null.");
        }

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) {
                throw new IllegalArgumentException("Matrix row " + i + " must not be null.");
            }

            if (matrix[i].length != matrix.length) {
                throw new IllegalArgumentException("Matrix must be square.");
            }
        }
    }

    private static void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Matrix index out of bounds: " + index);
        }
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}
