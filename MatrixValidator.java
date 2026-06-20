final class MatrixValidator {
    private MatrixValidator() {
    }

    static int requireSquare(int[][] matrix, String name) {
        int columnCount = requireRectangular(matrix, name);

        if (matrix.length != columnCount) {
            throw new IllegalArgumentException(name + " must be square.");
        }

        return matrix.length;
    }

    static int requireRectangular(int[][] matrix, String name) {
        if (matrix == null) {
            throw new IllegalArgumentException(name + " cannot be null.");
        }

        if (matrix.length == 0) {
            return 0;
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

        return columnCount;
    }
}
