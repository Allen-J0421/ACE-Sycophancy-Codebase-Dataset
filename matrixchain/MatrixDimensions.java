package matrixchain;

final class MatrixDimensions {
    final int rows;
    final int cols;

    MatrixDimensions(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive, got: " + rows + "x" + cols);
        }
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public String toString() {
        return rows + "x" + cols;
    }
}
