package countingsort;

public class CountingSort {

    public static int[] sort(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Input array cannot be null");
        if (arr.length == 0) return new int[0];

        int min = arr[0], max = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
            if (val > max) max = val;
        }

        int range = max - min + 1;
        int[] count = new int[range];
        for (int val : arr) {
            count[val - min]++;
        }

        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }

        int[] sorted = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            int val = arr[i];
            sorted[count[val - min] - 1] = val;
            count[val - min]--;
        }

        return sorted;
    }
}
