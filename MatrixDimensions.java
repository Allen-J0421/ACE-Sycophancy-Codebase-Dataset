import java.util.Arrays;

final class MatrixDimensions {

    private final int[] values;

    private MatrixDimensions(int[] values) {
        this.values = values;
    }

    static MatrixDimensions of(int[] dimensions) {
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

    static MatrixDimensions parse(String[] args) {
        int[] dimensions = new int[args.length];

        for (int index = 0; index < args.length; index++) {
            dimensions[index] = Integer.parseInt(args[index]);
        }

        return of(dimensions);
    }

    int count() {
        return values.length;
    }

    int at(int index) {
        return values[index];
    }

    int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }
}
