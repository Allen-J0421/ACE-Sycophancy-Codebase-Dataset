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
        int right = sortedValues.size() - 1;

        while (left < right) {
            long sum = sortedValues.sumAt(left, right);

            if (sum == target) {
                return Optional.of(sortedValues.pairAt(left, right));
            }

            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return Optional.empty();
    }
}
