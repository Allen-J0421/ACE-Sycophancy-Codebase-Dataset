import java.util.Arrays;

public final class MatrixDimensions {

    private final int[] values;
    private final int size;

    private MatrixDimensions(int[] values) {
        this.values = values;
        this.size = values.length;
    }

    public static MatrixDimensions of(int... dimensions) {
        if (dimensions == null) {
            throw new IllegalArgumentException("Matrix dimensions cannot be null.");
        }

        int[] copy = Arrays.copyOf(dimensions, dimensions.length);
        for (int dimension : copy) {
            if (dimension <= 0) {
                throw new IllegalArgumentException("Matrix dimensions must be positive.");
            }
        }

        return new MatrixDimensions(copy);
    }

    public static MatrixDimensions parse(String[] args) {
        int[] dimensions = new int[args.length];

        for (int index = 0; index < args.length; index++) {
            dimensions[index] = parseDimension(args[index], index);
        }

        return of(dimensions);
    }

    public int size() {
        return size;
    }

    public int matrixCount() {
        return Math.max(0, size - 1);
    }

    public boolean isTrivialChain() {
        return matrixCount() <= 1;
    }

    public String matrixLabel(int matrixIndex) {
        return "A" + (matrixIndex + 1);
    }

    public int valueAt(int index) {
        return values[index];
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    private static int parseDimension(String value, int index) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                "Invalid matrix dimension at argument " + index + ": " + value,
                exception
            );
        }
    }
}
