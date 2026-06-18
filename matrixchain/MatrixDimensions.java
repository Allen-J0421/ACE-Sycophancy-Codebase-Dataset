package matrixchain;

import java.util.Arrays;
import java.util.Objects;

public final class MatrixDimensions {

    private final int[] values;

    public MatrixDimensions(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length < 2) {
            throw new IllegalArgumentException(
                    "At least two dimensions are required to describe one matrix");
        }
        for (int value : values) {
            if (value <= 0) {
                throw new IllegalArgumentException("Matrix dimensions must be positive");
            }
        }
        this.values = Arrays.copyOf(values, values.length);
    }

    public int[] values() {
        return Arrays.copyOf(values, values.length);
    }

    public int matrixCount() {
        return values.length - 1;
    }

    public int[] toArray() {
        return values();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MatrixDimensions that
                && Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "MatrixDimensions" + Arrays.toString(values);
    }
}
