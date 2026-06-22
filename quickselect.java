import java.util.Arrays;

final class QuickSelect {

    private static final PivotSelector DEFAULT_PIVOT_SELECTOR =
        new MedianOfThreePivotSelector();

    private QuickSelect() {
        // Utility class.
    }

    public static int selectKthSmallest(int[] values, int rank) {
        return SelectionRequest.copying(values, rank).createSession().select();
    }

    public static int selectKthSmallestInPlace(int[] values, int rank) {
        return SelectionRequest.inPlace(values, rank).createSession().select();
    }

    public static int kthSmallest(int[] values, int k) {
        return selectKthSmallest(values, k);
    }

    private static void swap(int[] values, int left, int right) {
        if (left == right) {
            return;
        }

        int temp = values[left];
        values[left] = values[right];
        values[right] = temp;
    }

    private static final class SelectionSession {

        private final int[] values;
        private final int targetIndex;
        private final PivotSelector pivotSelector;
        private int low;
        private int high;

        private SelectionSession(int[] values, int targetIndex, PivotSelector pivotSelector) {
            this.values = values;
            this.targetIndex = targetIndex;
            this.pivotSelector = pivotSelector;
            this.low = 0;
            this.high = values.length - 1;
        }

        private int select() {
            while (isSearchActive()) {
                int partitionIndex = partitionCurrentRange();

                if (partitionIndex == targetIndex) {
                    return values[partitionIndex];
                }

                narrowSearchRange(partitionIndex);
            }

            throw new IllegalStateException("Quickselect failed to locate the target index");
        }

        private boolean isSearchActive() {
            return low <= high;
        }

        private int partitionCurrentRange() {
            movePivotToEnd();
            int pivot = values[high];
            int pivotIndex = low;

            for (int i = low; i < high; i++) {
                if (values[i] < pivot) {
                    swap(values, i, pivotIndex);
                    pivotIndex++;
                }
            }

            swap(values, high, pivotIndex);
            return pivotIndex;
        }

        private void movePivotToEnd() {
            swap(values, pivotSelector.selectPivotIndex(values, low, high), high);
        }

        private void narrowSearchRange(int partitionIndex) {
            if (partitionIndex < targetIndex) {
                low = partitionIndex + 1;
            } else {
                high = partitionIndex - 1;
            }
        }
    }

    private static final class SelectionRequest {

        private final int[] values;
        private final int targetIndex;

        private SelectionRequest(int[] values, int targetIndex) {
            this.values = values;
            this.targetIndex = targetIndex;
        }

        private static SelectionRequest copying(int[] values, int rank) {
            validateRank(values, rank);
            return new SelectionRequest(Arrays.copyOf(values, values.length), rank - 1);
        }

        private static SelectionRequest inPlace(int[] values, int rank) {
            validateRank(values, rank);
            return new SelectionRequest(values, rank - 1);
        }

        private SelectionSession createSession() {
            return new SelectionSession(values, targetIndex, DEFAULT_PIVOT_SELECTOR);
        }
    }

    private interface PivotSelector {

        int selectPivotIndex(int[] values, int low, int high);
    }

    private static final class MedianOfThreePivotSelector implements PivotSelector {

        private MedianOfThreePivotSelector() {
            // Utility class.
        }

        @Override
        public int selectPivotIndex(int[] values, int low, int high) {
            int mid = low + (high - low) / 2;

            order(values, low, mid);
            order(values, low, high);
            order(values, mid, high);

            return mid;
        }

        private static void order(int[] values, int left, int right) {
            if (values[left] > values[right]) {
                swap(values, left, right);
            }
        }
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

final class QuickSelectDemo {

    private QuickSelectDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int rank = 3;

        System.out.println(
            "K-th smallest element in array: "
            + QuickSelect.selectKthSmallest(values, rank));
    }
}
