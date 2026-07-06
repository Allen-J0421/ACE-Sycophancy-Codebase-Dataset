import java.util.Arrays;

public class InsertionSort {

    static void sort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int insertAt = shiftGreaterElementsRight(arr, i - 1, key);
            arr[insertAt] = key;
        }
    }

    static int shiftGreaterElementsRight(int[] arr, int from, int key) {
        int j = from;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        return j + 1;
    }

    public static void main(String[] args) {
        int[] arr = { 12, 11, 13, 5, 6 };
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
