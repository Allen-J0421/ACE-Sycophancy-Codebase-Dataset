import java.util.Optional;

public final class TwoPointers {

    private TwoPointers() {
    }

    public static Optional<PairMatch> findPairWithSum(int[] sortedValues, long target) {
        return findPairWithSum(copySortedValues(sortedValues), target);
    }

    public static Optional<PairMatch> findPairWithSum(SortedIntArray sortedValues, long target) {
        return searchIn(requireSortedValues(sortedValues), target);
    }

    public static boolean hasPairWithSum(int[] sortedValues, long target) {
        return hasPairWithSum(copySortedValues(sortedValues), target);
    }

    public static boolean hasPairWithSum(SortedIntArray sortedValues, long target) {
        return containsIn(requireSortedValues(sortedValues), target);
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

    private static Optional<PairMatch> searchIn(SortedIntArray sortedValues, long target) {
        return sortedValues.findPairWithSum(target);
    }

    private static boolean containsIn(SortedIntArray sortedValues, long target) {
        return sortedValues.hasPairWithSum(target);
    }
}
