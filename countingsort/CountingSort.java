package countingsort;

public class CountingSort {

    private CountingSort() {}

    private static final class SortState {
        final int[] arr;
        final int min;
        final int[] counts;

        SortState(int[] arr, int min, int[] counts) {
            this.arr = arr;
            this.min = min;
            this.counts = counts;
        }

        void accumulateCounts() {
            for (int i = 1; i < counts.length; i++) {
                counts[i] += counts[i - 1];
            }
        }

        int[] buildSorted() {
            int[] sorted = new int[arr.length];
            for (int i = arr.length - 1; i >= 0; i--) {
                int idx = arr[i] - min;
                sorted[counts[idx] - 1] = arr[i];
                counts[idx]--;
            }
            return sorted;
        }
    }

    public static int[] sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (arr.length == 0) {
            return new int[0];
        }

        int min = computeMin(arr);
        SortState state = new SortState(arr, min, computeFrequencies(arr, min, computeRange(arr, min)));
        state.accumulateCounts();
        return state.buildSorted();
    }

    private static int computeMin(int[] arr) {
        int min = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
        }
        return min;
    }

    private static int computeRange(int[] arr, int min) {
        int max = arr[0];
        for (int val : arr) {
            if (val > max) max = val;
        }
        long range = (long) max - min + 1;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Value range too large for counting sort: " + range);
        }
        return (int) range;
    }

    private static int[] computeFrequencies(int[] arr, int min, int range) {
        int[] counts = new int[range];
        for (int val : arr) {
            counts[val - min]++;
        }
        return counts;
    }
}
