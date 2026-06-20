final class Matrices {
    private Matrices() {
    }

    static int requireSquare(int[][] matrix, String label) {
        return MatrixValidator.requireSquare(matrix, label);
    }

    static int requireRectangular(int[][] matrix, String label) {
        return MatrixValidator.requireRectangular(matrix, label);
    }

    static int[][] copyOf(int[][] matrix) {
        return MatrixUtils.copyOf(matrix);
    }

    static int[][] copyRows(int[][] matrix) {
        return MatrixUtils.copyRows(matrix);
    }
}
