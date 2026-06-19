package matrixchain;

import java.util.Arrays;

/**
 * Immutable, validated view over a matrix-chain dimension array.
 *
 * <p>A chain of {@code m} matrices is described by {@code m + 1} dimensions,
 * where matrix {@code i} (0-based) has shape {@code at(i) x at(i + 1)}.
 */
final class MatrixDimensions {

    private final int[] dimensions;

    private MatrixDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Creates a validated dimension chain.
     *
     * @throws IllegalArgumentException if the array is null, has fewer than two
     *                                  entries, or contains a non-positive value
     */
    static MatrixDimensions of(int... dimensions) {
        if (dimensions == null) {
            throw new IllegalArgumentException("dimensions must not be null");
        }
        if (dimensions.length < 2) {
            throw new IllegalArgumentException(
                "a matrix chain needs at least two dimensions; got " + dimensions.length);
        }
        for (int i = 0; i < dimensions.length; i++) {
            if (dimensions[i] <= 0) {
                throw new IllegalArgumentException(
                    "dimensions must be positive; found " + dimensions[i] + " at index " + i);
            }
        }
        return new MatrixDimensions(Arrays.copyOf(dimensions, dimensions.length));
    }

    /** Number of matrices in the chain. */
    int matrixCount() {
        return dimensions.length - 1;
    }

    /** The dimension at position {@code index} along the chain. */
    int at(int index) {
        return dimensions[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(dimensions);
    }
}
