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
        accumulateCounts(state);
        return buildSorted(state);
    }

    private static void accumulateCounts(SortState state) {
        for (int i = 1; i < state.counts.length; i++) {
            state.counts[i] += state.counts[i - 1];
        }
    }

    private static int[] buildSorted(SortState state) {
        int[] sorted = new int[state.arr.length];
        for (int i = state.arr.length - 1; i >= 0; i--) {
            int idx = state.arr[i] - state.min;
            sorted[state.counts[idx] - 1] = state.arr[i];
            state.counts[idx]--;
        }
        return sorted;
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
