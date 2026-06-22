class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int partition(int[] arr, int low, int high) {
        int pivotValue = arr[high];
        int pivotIndex = low;

        for (int i = low; i < high; i++) {
            if (arr[i] < pivotValue) {
                swap(arr, i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(arr, pivotIndex, high);
        return pivotIndex;
    }

    public static int kthSmallest(int[] arr, int low, int high, int k) {
        int partitionIndex = partition(arr, low, high);

        if (partitionIndex == k - 1) {
            return arr[partitionIndex];
        }

        if (partitionIndex < k - 1) {
            return kthSmallest(arr, partitionIndex + 1, high, k);
        }

        return kthSmallest(arr, low, partitionIndex - 1, k);
    }

    public static void main(String[] args) {
        int[] array = { 10, 4, 5, 8, 6, 11, 26 };
        int[] workingCopy = array.clone();

        int kPosition = 3;
        int length = array.length;

        if (kPosition < 1 || kPosition > length) {
            System.out.println("Index out of bound");
        } else {
            System.out.println(
                "K-th smallest element in array : "
                + kthSmallest(workingCopy, 0, length - 1, kPosition));
        }
    }
}
