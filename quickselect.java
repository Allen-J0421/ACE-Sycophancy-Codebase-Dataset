public final class QuickSelect {

    private static final PivotSelector DEFAULT_PIVOT_SELECTOR =
        MedianOfThreePivotSelector.INSTANCE;

    private QuickSelect() {
        // Utility class.
    }

    public static int selectKthSmallest(int[] values, int rank) {
        return SelectionRequest.copying(values, rank).selectWith(DEFAULT_PIVOT_SELECTOR);
    }

    public static int selectKthSmallestInPlace(int[] values, int rank) {
        return SelectionRequest.inPlace(values, rank).selectWith(DEFAULT_PIVOT_SELECTOR);
    }

    public static int kthSmallest(int[] values, int k) {
        return selectKthSmallest(values, k);
    }

    public static void main(String[] args) {
        QuickSelectDemo.main(args);
    }
}
