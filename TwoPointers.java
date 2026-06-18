import java.util.Optional;

public final class TwoPointers {

    private TwoPointers() {
    }

    public static Optional<PairMatch> findPairWithSum(int[] sortedValues, long target) {
        return SortedIntArray.copyOf(sortedValues).findPairWithSum(target);
    }

    public static Optional<PairMatch> findPairWithSum(SortedIntArray sortedValues, long target) {
        requireSortedValues(sortedValues);
        return sortedValues.findPairWithSum(target);
    }

    public static boolean hasPairWithSum(int[] sortedValues, long target) {
        return SortedIntArray.copyOf(sortedValues).hasPairWithSum(target);
    }

    public static boolean hasPairWithSum(SortedIntArray sortedValues, long target) {
        requireSortedValues(sortedValues);
        return sortedValues.hasPairWithSum(target);
    }

    private static void requireSortedValues(SortedIntArray sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }
    }
}
