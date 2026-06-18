package matrixchain;

import java.util.Arrays;
import java.util.Objects;

public record MatrixDimensions(int[] values) {

    public MatrixDimensions {
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
        values = Arrays.copyOf(values, values.length);
    }

    @Override
    public int[] values() {
        return Arrays.copyOf(values, values.length);
    }

    public int matrixCount() {
        return values.length - 1;
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }
}
