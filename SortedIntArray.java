import java.util.Arrays;

public final class SortedIntArray {

    private final int[] values;

    private SortedIntArray(int[] values) {
        this.values = values;
    }

    public static SortedIntArray copyOf(int[] values) {
        validateSorted(values);
        return new SortedIntArray(Arrays.copyOf(values, values.length));
    }

    public static SortedIntArray of(int... values) {
        return copyOf(values);
    }

    public int size() {
        return values.length;
    }

    public int get(int index) {
        return values[index];
    }

    public long sumAt(int leftIndex, int rightIndex) {
        validatePairIndexes(leftIndex, rightIndex);
        return (long) values[leftIndex] + values[rightIndex];
    }

    public PairMatch pairAt(int leftIndex, int rightIndex) {
        validatePairIndexes(leftIndex, rightIndex);
        return new PairMatch(leftIndex, rightIndex, values[leftIndex], values[rightIndex]);
    }

    public int[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    private void validatePairIndexes(int leftIndex, int rightIndex) {
        if (leftIndex < 0 || rightIndex < 0) {
            throw new IllegalArgumentException("pair indexes must not be negative");
        }

        if (rightIndex >= values.length) {
            throw new IllegalArgumentException("pair indexes must be within array bounds");
        }

        if (leftIndex >= rightIndex) {
            throw new IllegalArgumentException("leftIndex must be less than rightIndex");
        }
    }

    private static void validateSorted(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        for (int index = 1; index < values.length; index++) {
            if (values[index - 1] > values[index]) {
                throw new IllegalArgumentException(
                    "two-pointer search requires values sorted in nondecreasing order");
            }
        }
    }
}
