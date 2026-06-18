import java.util.Optional;

public final class TwoPointers {

    private TwoPointers() {
    }

    public static Optional<PairMatch> findPairWithSum(int[] sortedValues, long target) {
        return findPairWithSum(SortedIntArray.copyOf(sortedValues), target);
    }

    public static Optional<PairMatch> findPairWithSum(SortedIntArray sortedValues, long target) {
        requireSortedValues(sortedValues);
        return findPairInSortedValues(sortedValues, target);
    }

    public static boolean hasPairWithSum(int[] sortedValues, long target) {
        return findPairWithSum(sortedValues, target).isPresent();
    }

    public static boolean hasPairWithSum(SortedIntArray sortedValues, long target) {
        return findPairWithSum(sortedValues, target).isPresent();
    }

    private static void requireSortedValues(SortedIntArray sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }
    }

    private static Optional<PairMatch> findPairInSortedValues(SortedIntArray sortedValues, long target) {
        int left = 0;
        int right = sortedValues.length() - 1;

        while (left < right) {
            int leftValue = sortedValues.valueAt(left);
            int rightValue = sortedValues.valueAt(right);
            long sum = (long) leftValue + rightValue;

            if (sum == target) {
                return Optional.of(new PairMatch(left, right, leftValue, rightValue));
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return Optional.empty();
    }

    public record PairMatch(int leftIndex, int rightIndex, int leftValue, int rightValue) {
        public long sum() {
            return (long) leftValue + rightValue;
        }
    }
}
