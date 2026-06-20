final class MatrixValidator {
    private MatrixValidator() {
    }

    static void requireSquare(int[][] matrix, String name) {
        requireRectangular(matrix, name);

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row].length != matrix.length) {
                throw new IllegalArgumentException(name + " must be square.");
            }
        }
    }

    static void requireRectangular(int[][] matrix, String name) {
        if (matrix == null) {
            throw new IllegalArgumentException(name + " cannot be null.");
        }

        if (matrix.length == 0) {
            return;
        }

        if (matrix[0] == null) {
            throw new IllegalArgumentException(name + " rows cannot be null.");
        }

        int columnCount = matrix[0].length;
        for (int row = 1; row < matrix.length; row++) {
            if (matrix[row] == null) {
                throw new IllegalArgumentException(name + " rows cannot be null.");
            }
            if (matrix[row].length != columnCount) {
                throw new IllegalArgumentException(name + " must be rectangular.");
            }
        }
    }
}
