package maxflow;

final class SquareMatrix {
    private final int[][] values;

    private SquareMatrix(int[][] values) {
        this.values = values;
    }

    static SquareMatrix copyOf(int[][] source, String label) {
        validateSquareMatrix(source, label);
        return new SquareMatrix(deepCopy(source));
    }

    SquareMatrix copy() {
        return new SquareMatrix(deepCopy(values));
    }

    int size() {
        return values.length;
    }

    int get(int row, int col) {
        return values[row][col];
    }

    boolean hasPositiveValue(int row, int col) {
        return values[row][col] > 0;
    }

    void add(int row, int col, int delta) {
        values[row][col] += delta;
    }

    int[][] snapshot() {
        return deepCopy(values);
    }

    private static void validateSquareMatrix(int[][] matrix, String label) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException(label + " must not be null or empty");
        }

        if (matrix[0] == null) {
            throw new IllegalArgumentException(label + " rows must not be null");
        }

        int width = matrix[0].length;
        if (width == 0) {
            throw new IllegalArgumentException(label + " must contain at least one vertex");
        }

        if (width != matrix.length) {
            throw new IllegalArgumentException(label + " must be square");
        }

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row] == null || matrix[row].length != width) {
                throw new IllegalArgumentException(label + " must be rectangular");
            }
        }
    }

    private static int[][] deepCopy(int[][] source) {
        int[][] copy = new int[source.length][source.length];
        for (int row = 0; row < source.length; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, source[row].length);
        }
        return copy;
    }
}
