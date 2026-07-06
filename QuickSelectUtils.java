import java.util.Random;

class QuickSelectUtils {

    private static final Random rand = new Random();

    private static void swap(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int partition(int[] arr, int low, int high)
    {
        int randomIndex = low + rand.nextInt(high - low + 1);
        swap(arr, randomIndex, high);

        int pivot = arr[high], pivotloc = low;
        for (int i = low; i <= high; i++) {

            if (arr[i] < pivot) {
                swap(arr, i, pivotloc);
                pivotloc++;
            }
        }

        swap(arr, high, pivotloc);

        return pivotloc;
    }

    public static int kthSmallest(int[] arr, int low, int high, int k)
    {
        if (arr == null || arr.length == 0)
            throw new IllegalArgumentException("Array must not be null or empty");
        if (k < 1 || k > arr.length)
            throw new IllegalArgumentException(
                "k must be between 1 and " + arr.length + ", got " + k);

        int partition = partition(arr, low, high);

        if (partition == k - 1)
            return arr[partition];

        else if (partition < k - 1)
            return kthSmallest(arr, partition + 1, high, k);

        else
            return kthSmallest(arr, low, partition - 1, k);
    }
}
