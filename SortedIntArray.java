import java.util.Arrays;
import java.util.Optional;

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

    public Optional<PairMatch> findPairWithSum(long target) {
        SearchWindow window = new SearchWindow();

        while (window.isOpen()) {
            long sum = window.sum();

            if (sum == target) {
                return Optional.of(window.toPairMatch());
            }

            window.moveToward(target, sum);
        }

        return Optional.empty();
    }

    public boolean hasPairWithSum(long target) {
        return findPairWithSum(target).isPresent();
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SortedIntArray that)) {
            return false;
        }

        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
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

    private final class SearchWindow {

        private int left = 0;
        private int right = values.length - 1;

        private boolean isOpen() {
            return left < right;
        }

        private long sum() {
            return (long) values[left] + values[right];
        }

        private PairMatch toPairMatch() {
            return new PairMatch(left, right, values[left], values[right]);
        }

        private void moveToward(long target, long sum) {
            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }
}
