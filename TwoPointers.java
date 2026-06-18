import java.util.Optional;

public final class TwoPointers {

    private TwoPointers() {
    }

    public static Optional<PairMatch> findPairWithSum(int[] sortedValues, long target) {
        return copySortedValues(sortedValues).findPairWithSum(target);
    }

    public static Optional<PairMatch> findPairWithSum(SortedIntArray sortedValues, long target) {
        return requireSortedValues(sortedValues).findPairWithSum(target);
    }

    public static boolean hasPairWithSum(int[] sortedValues, long target) {
        return copySortedValues(sortedValues).hasPairWithSum(target);
    }

    public static boolean hasPairWithSum(SortedIntArray sortedValues, long target) {
        return requireSortedValues(sortedValues).hasPairWithSum(target);
    }

    private static SortedIntArray copySortedValues(int[] sortedValues) {
        return SortedIntArray.copyOf(sortedValues);
    }

    private static SortedIntArray requireSortedValues(SortedIntArray sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }

        return sortedValues;
    }
}
