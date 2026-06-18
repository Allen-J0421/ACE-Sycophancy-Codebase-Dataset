import java.util.Arrays;
import java.util.Optional;

public final class SortedIntArray {

    private static final SortedIntArray EMPTY = new SortedIntArray(new int[0]);

    private final int[] values;

    private SortedIntArray(int[] values) {
        this.values = values;
    }

    public static SortedIntArray copyOf(int[] values) {
        validateSorted(values);
        if (values.length == 0) {
            return EMPTY;
        }

        return new SortedIntArray(Arrays.copyOf(values, values.length));
    }

    public static SortedIntArray of(int... values) {
        return copyOf(values);
    }

    public int size() {
        return values.length;
    }

    public Optional<PairMatch> findPairWithSum(long target) {
        if (!canContainPairMatching(target)) {
            return Optional.empty();
        }

        int left = 0;
        int right = values.length - 1;

        while (left < right) {
            long sum = sumAt(left, right);
            if (sum == target) {
                return Optional.of(createPairMatch(left, right));
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return Optional.empty();
    }

    public boolean hasPairWithSum(long target) {
        return findPairWithSum(target).isPresent();
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

    private boolean canContainPairMatching(long target) {
        return values.length >= 2
            && target >= minimumPairSum()
            && target <= maximumPairSum();
    }

    private long minimumPairSum() {
        return (long) values[0] + values[1];
    }

    private long maximumPairSum() {
        int lastIndex = values.length - 1;
        return (long) values[lastIndex - 1] + values[lastIndex];
    }

    private long sumAt(int leftIndex, int rightIndex) {
        return (long) values[leftIndex] + values[rightIndex];
    }

    private PairMatch createPairMatch(int leftIndex, int rightIndex) {
        return new PairMatch(leftIndex, rightIndex, values[leftIndex], values[rightIndex]);
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
