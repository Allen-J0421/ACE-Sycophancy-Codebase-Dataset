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
            dimensions[index] = Integer.parseInt(args[index]);
        }

        return of(dimensions);
    }

    public int size() {
        return size;
    }

    public int valueAt(int index) {
        return values[index];
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }
}
