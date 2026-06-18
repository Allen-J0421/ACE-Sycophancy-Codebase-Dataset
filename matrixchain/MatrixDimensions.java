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

    boolean isCompatibleWith(MatrixDimensions next) {
        return this.cols == next.rows;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MatrixDimensions)) return false;
        MatrixDimensions other = (MatrixDimensions) obj;
        return rows == other.rows && cols == other.cols;
    }

    @Override
    public int hashCode() {
        return 31 * rows + cols;
    }

    @Override
    public String toString() {
        return rows + "x" + cols;
    }
}
