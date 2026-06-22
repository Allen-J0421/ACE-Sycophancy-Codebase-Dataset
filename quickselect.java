import java.util.Arrays;

public final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    public static int selectKthSmallest(int[] values, int rank) {
        validateRank(values, rank);
        return selectValidated(Arrays.copyOf(values, values.length), rank);
    }

    public static int selectKthSmallestInPlace(int[] values, int rank) {
        validateRank(values, rank);
        return selectValidated(values, rank);
    }

    public static int kthSmallest(int[] values, int k) {
        return selectKthSmallest(values, k);
    }

    private static int selectValidated(int[] values, int rank) {
        return new QuickSelectEngine(values, rank - 1, MedianOfThreePivotSelector.INSTANCE)
            .select();
    }

    private static void validateRank(int[] values, int rank) {
        if (values == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Input array must not be empty");
        }

        if (rank < 1 || rank > values.length) {
            throw new IllegalArgumentException(
                "rank must be between 1 and " + values.length + " inclusive");
        }
    }

    public static void main(String[] args) {
        QuickSelectDemo.main(args);
    }
}
