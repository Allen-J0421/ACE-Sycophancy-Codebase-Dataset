import java.util.Arrays;
import java.util.Objects;

public final class CountingSort {

    private CountingSort() {
        // Utility class.
    }

    public static int[] countSort(int[] arr) {
        Objects.requireNonNull(arr, "arr");
        if (arr.length == 0) {
            return new int[0];
        }

        int minVal = arr[0];
        int maxVal = arr[0];
        for (int value : arr) {
            if (value < minVal) {
                minVal = value;
            }
            if (value > maxVal) {
                maxVal = value;
            }
        }

        int range = maxVal - minVal + 1;
        int[] counts = new int[range];
        for (int value : arr) {
            counts[value - minVal]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        int[] sorted = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            int value = arr[i];
            int countIndex = value - minVal;
            sorted[--counts[countIndex]] = value;
        }

        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] ans = countSort(arr);
        System.out.println(Arrays.toString(ans));
    }
}
