public class QuickSelect {

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int storeIndex = low;
        for (int i = low; i < high; i++) {
            if (arr[i] < pivot) {
                swap(arr, i, storeIndex);
                storeIndex++;
            }
        }
        swap(arr, storeIndex, high);
        return storeIndex;
    }

    private static int select(int[] arr, int low, int high, int k) {
        int pivotIndex = partition(arr, low, high);
        if (pivotIndex == k - 1)
            return arr[pivotIndex];
        else if (pivotIndex < k - 1)
            return select(arr, pivotIndex + 1, high, k);
        else
            return select(arr, low, pivotIndex - 1, k);
    }

    public static int kthSmallest(int[] arr, int k) {
        if (arr == null || arr.length == 0)
            throw new IllegalArgumentException("Array must be non-empty");
        if (k < 1 || k > arr.length)
            throw new IllegalArgumentException("k must be between 1 and " + arr.length);
        int[] copy = arr.clone();
        return select(copy, 0, copy.length - 1, k);
    }

    public static void main(String[] args) {
        int[] array = { 10, 4, 5, 8, 6, 11, 26 };
        int k = 3;
        System.out.println("K-th smallest element in array : " + kthSmallest(array, k));
    }
}
