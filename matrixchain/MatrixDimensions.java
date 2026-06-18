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

    public static MatrixDimensions of(int... values) {
        return new MatrixDimensions(values);
    }

    public static MatrixDimensions parse(String... tokens) {
        Objects.requireNonNull(tokens, "tokens");
        if (tokens.length < 2) {
            throw new IllegalArgumentException(
                    "Expected at least two dimensions, for example: 10 20 30");
        }

        int[] values = new int[tokens.length];
        for (int index = 0; index < tokens.length; index++) {
            values[index] = parseDimension(tokens[index], index);
        }
        return new MatrixDimensions(values);
    }

    public int[] values() {
        return Arrays.copyOf(values, values.length);
    }

    public int matrixCount() {
        return values.length - 1;
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

    private static int parseDimension(String token, int index) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid dimension at position " + (index + 1) + ": " + token, exception);
        }
    }
}
