import java.util.Arrays;

class CountingSort {

    public static int[] countSort(int[] arr) {
        if (arr.length == 0) {
            return new int[0];
        }

        int[] counts = buildCumulativeCounts(arr, findMax(arr));
        return buildSortedArray(arr, counts);
    }

    private static int findMax(int[] arr) {
        int maxVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxVal) {
                maxVal = arr[i];
            }
        }

        return maxVal;
    }

    private static int[] buildCumulativeCounts(int[] arr, int maxVal) {
        int[] counts = new int[maxVal + 1];
        for (int value : arr) {
            counts[value]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        return counts;
    }

    private static int[] buildSortedArray(int[] arr, int[] counts) {
        int[] sorted = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            int value = arr[i];
            sorted[counts[value] - 1] = value;
            counts[value]--;
        }

        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] sorted = countSort(arr);
        System.out.println(Arrays.toString(sorted));
    }
}
