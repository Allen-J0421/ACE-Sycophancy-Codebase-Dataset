import java.util.Arrays;

final class MatrixUtils {
    private MatrixUtils() {
    }

    static int[][] copyOf(int[][] matrix) {
        MatrixValidator.requireRectangular(matrix, "Matrix");
        return copyRows(matrix);
    }

    static int[][] copyRows(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix must not be null.");
        }

        int[][] copy = new int[matrix.length][];
        for (int rowIndex = 0; rowIndex < matrix.length; rowIndex++) {
            int[] row = matrix[rowIndex];
            copy[rowIndex] = row == null ? null : Arrays.copyOf(row, row.length);
        }

        return copy;
    }

    static boolean hasNegativeDiagonalEntry(int[][] matrix) {
        for (int index = 0; index < matrix.length; index++) {
            if (matrix[index][index] < 0) {
                return true;
            }
        }

        return false;
    }
}
