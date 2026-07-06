package countingsort;

public class CountingSort {

    private CountingSort() {}

    private interface SortState {
        void accumulateCounts();
        int[] buildSorted();

        static int[] sort(int[] arr) {
            if (arr == null) {
                throw new IllegalArgumentException("Input array cannot be null");
            }
            if (arr.length == 0) {
                return new int[0];
            }
            SortState state = new CountingSortState(arr);
            state.accumulateCounts();
            return state.buildSorted();
        }
    }

    private static final class CountingSortState implements SortState {
        final int[] arr;
        final int min;
        final int[] counts;

        CountingSortState(int[] arr) {
            int min = arr[0];
            int max = arr[0];
            for (int val : arr) {
                if (val < min) min = val;
                if (val > max) max = val;
            }
            long range = (long) max - min + 1;
            if (range > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Value range too large for counting sort: " + range);
            }
            this.arr = arr;
            this.min = min;
            this.counts = new int[(int) range];
            for (int val : arr) {
                this.counts[val - min]++;
            }
        }

        @Override
        public void accumulateCounts() {
            for (int i = 1; i < counts.length; i++) {
                counts[i] += counts[i - 1];
            }
        }

        @Override
        public int[] buildSorted() {
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
        return SortState.sort(arr);
    }
}
