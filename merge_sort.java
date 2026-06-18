import java.util.Arrays;
import java.util.Comparator;

class MergeSort {

    private static final int INSERTION_SORT_THRESHOLD = 10;

    private MergeSort() {}

    // --- int[] ---

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        mergeSort(arr, new int[arr.length], 0, arr.length - 1);
    }

    public static void sort(int[] arr, int fromIndex, int toIndex) {
        if (arr == null) throw new NullPointerException("arr must not be null");
        validateRange(arr.length, fromIndex, toIndex);
        if (toIndex - fromIndex <= 1) return;
        mergeSort(arr, new int[arr.length], fromIndex, toIndex - 1);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (right - left < INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, left, right);
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) temp[i] = arr[i];
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            arr[k++] = temp[i] <= temp[j] ? temp[i++] : temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
    }

    // --- Generic ---

    public static <T extends Comparable<T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<T> comparator) {
        if (arr == null || arr.length <= 1) return;
        sort(arr, 0, arr.length, comparator);
    }

    public static <T extends Comparable<T>> void sort(T[] arr, int fromIndex, int toIndex) {
        sort(arr, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, int fromIndex, int toIndex, Comparator<T> comparator) {
        if (arr == null) throw new NullPointerException("arr must not be null");
        validateRange(arr.length, fromIndex, toIndex);
        if (toIndex - fromIndex <= 1) return;
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[arr.length];
        mergeSort(arr, temp, comparator, fromIndex, toIndex - 1);
    }

    private static <T> void mergeSort(T[] arr, T[] temp, Comparator<T> comparator, int left, int right) {
        if (right - left < INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, comparator, left, right);
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, comparator, left, mid);
        mergeSort(arr, temp, comparator, mid + 1, right);
        merge(arr, temp, comparator, left, mid, right);
    }

    private static <T> void insertionSort(T[] arr, Comparator<T> comparator, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= left && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    private static <T> void merge(T[] arr, T[] temp, Comparator<T> comparator, int left, int mid, int right) {
        for (int i = left; i <= right; i++) temp[i] = arr[i];
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            arr[k++] = comparator.compare(temp[i], temp[j]) <= 0 ? temp[i++] : temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
    }

    // --- Validation ---

    private static void validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new ArrayIndexOutOfBoundsException(
                "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex " + fromIndex + " > toIndex " + toIndex);
        }
    }

    public static void main(String[] args) {
        int[] ints = {38, 27, 43, 10, 5, 1, 9, 3};
        sort(ints);
        System.out.println(Arrays.toString(ints));

        String[] words = {"banana", "apple", "cherry", "date"};
        sort(words);
        System.out.println(Arrays.toString(words));

        Integer[] nums = {5, 3, 8, 1, 9, 2};
        sort(nums, Comparator.reverseOrder());
        System.out.println(Arrays.toString(nums));

        // range sort: only the middle three elements
        int[] partial = {9, 5, 1, 7, 3, 8, 2};
        sort(partial, 2, 5);
        System.out.println(Arrays.toString(partial));
    }
}
