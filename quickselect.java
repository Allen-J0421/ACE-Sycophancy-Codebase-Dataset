import java.util.Arrays;

public final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    public static int selectKthSmallest(int[] values, int rank) {
        return SelectionRequest.copying(values, rank).select();
    }

    public static int selectKthSmallestInPlace(int[] values, int rank) {
        return SelectionRequest.inPlace(values, rank).select();
    }

    public static int kthSmallest(int[] values, int k) {
        return selectKthSmallest(values, k);
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

    private static final class SelectionRequest {

        private final int[] values;
        private final int rank;

        private SelectionRequest(int[] values, int rank) {
            this.values = values;
            this.rank = rank;
        }

        private static SelectionRequest copying(int[] values, int rank) {
            validateRank(values, rank);
            return new SelectionRequest(Arrays.copyOf(values, values.length), rank);
        }

        private static SelectionRequest inPlace(int[] values, int rank) {
            validateRank(values, rank);
            return new SelectionRequest(values, rank);
        }

        private int select() {
            return QuickSelectEngine.select(values, rank - 1);
        }
    }

    public static void main(String[] args) {
        QuickSelectDemo.main(args);
    }
}
