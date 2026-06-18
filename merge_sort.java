import java.util.Comparator;

class MergeSort {

    private MergeSort() {}

    // --- int[] overload (primitive, no boxing) ---

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, temp, left, mid);
            mergeSort(arr, temp, mid + 1, right);
            merge(arr, temp, left, mid, right);
        }
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (temp[i] <= temp[j]) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = temp[i++];
        }
        // remaining right-half elements are already in place
    }

    // --- Generic overloads ---

    public static <T extends Comparable<T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<T> comparator) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[arr.length];
        mergeSort(arr, temp, comparator, 0, arr.length - 1);
    }

    private static <T> void mergeSort(T[] arr, T[] temp, Comparator<T> comparator, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, temp, comparator, left, mid);
            mergeSort(arr, temp, comparator, mid + 1, right);
            merge(arr, temp, comparator, left, mid, right);
        }
    }

    private static <T> void merge(T[] arr, T[] temp, Comparator<T> comparator, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (comparator.compare(temp[i], temp[j]) <= 0) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = temp[i++];
        }
    }

    // --- Helpers ---

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(arr[i]);
        }
        System.out.println(sb);
    }

    private static <T> void printArray(T[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(arr[i]);
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        int[] ints = {38, 27, 43, 10, 5, 1, 9, 3};
        sort(ints);
        printArray(ints);

        String[] words = {"banana", "apple", "cherry", "date"};
        sort(words);
        printArray(words);

        Integer[] nums = {5, 3, 8, 1, 9, 2};
        sort(nums, Comparator.reverseOrder());
        printArray(nums);
    }
}
