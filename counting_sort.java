import java.util.Arrays;

final class CountingSort {

    private CountingSort() {
    }

    public static int[] countSort(int[] arr) {
        if (arr.length == 0) {
            return new int[0];
        }

        Bounds bounds = findBounds(arr);
        int[] counts = buildCumulativeCounts(arr, bounds);
        return buildSortedArray(arr, counts, bounds.min);
    }

    private static Bounds findBounds(int[] arr) {
        int minVal = arr[0];
        int maxVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < minVal) {
                minVal = arr[i];
            }
            if (arr[i] > maxVal) {
                maxVal = arr[i];
            }
        }

        return new Bounds(minVal, maxVal);
    }

    private static int[] buildCumulativeCounts(int[] arr, Bounds bounds) {
        int[] counts = new int[bounds.size()];
        for (int value : arr) {
            counts[value - bounds.min]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        return counts;
    }

    private static int[] buildSortedArray(int[] arr, int[] counts, int minVal) {
        int[] sorted = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            int value = arr[i];
            int countIndex = value - minVal;
            sorted[counts[countIndex] - 1] = value;
            counts[countIndex]--;
        }

        return sorted;
    }

    private static final class Bounds {
        private final int min;
        private final int max;

        private Bounds(int min, int max) {
            this.min = min;
            this.max = max;
        }

        private int size() {
            return max - min + 1;
        }
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] sorted = countSort(arr);
        System.out.println(Arrays.toString(sorted));
    }
}
