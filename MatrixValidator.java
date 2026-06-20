final class MatrixValidator {
    private MatrixValidator() {
    }

    static int requireSquare(int[][] matrix, String label) {
        return Matrices.requireSquare(matrix, label);
    }

    static int requireRectangular(int[][] matrix, String label) {
        return Matrices.requireRectangular(matrix, label);
    }
}
