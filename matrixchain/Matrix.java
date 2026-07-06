package matrixchain;

public record Matrix(int rows, int cols) {

    public Matrix {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException(
                "Matrix dimensions must be positive, got " + rows + "x" + cols);
        }
    }
}
