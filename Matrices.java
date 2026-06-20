import java.util.Arrays;

final class Matrices {
    private Matrices() {
    }

    static int requireSquare(int[][] matrix, String label) {
        int columnCount = requireRectangular(matrix, label);
        if (matrix.length != columnCount) {
            throw new IllegalArgumentException(label + " must be square.");
        }
        return matrix.length;
    }

    static int requireRectangular(int[][] matrix, String label) {
        if (matrix == null) {
            throw new IllegalArgumentException(label + " must not be null.");
        }

        if (matrix.length == 0) {
            return 0;
        }

        if (matrix[0] == null) {
            throw new IllegalArgumentException(label + " must not contain null rows.");
        }

        int columnCount = matrix[0].length;
        for (int rowIndex = 1; rowIndex < matrix.length; rowIndex++) {
            if (matrix[rowIndex] == null) {
                throw new IllegalArgumentException(label + " must not contain null rows.");
            }
            if (matrix[rowIndex].length != columnCount) {
                throw new IllegalArgumentException(label + " must be rectangular.");
            }
        }

        return columnCount;
    }

    static int[][] copyOf(int[][] matrix) {
        requireRectangular(matrix, "Matrix");

        int[][] copy = new int[matrix.length][];
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            copy[rowIndex] = Arrays.copyOf(matrix[rowIndex], matrix[rowIndex].length);
        }

        return copy;
    }
}
