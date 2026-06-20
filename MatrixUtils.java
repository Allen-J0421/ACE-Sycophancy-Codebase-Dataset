import java.util.Arrays;

final class MatrixUtils {
    private MatrixUtils() {
    }

    static int[][] copyOf(int[][] matrix) {
        MatrixValidator.requireRectangular(matrix, "Matrix");

        int[][] copy = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            copy[row] = Arrays.copyOf(matrix[row], matrix[row].length);
        }
        return copy;
    }
}
