class QuickSelectUtils {

    public static int kthSmallest(int[] arr, int low, int high, int k,
                                  PartitionStrategy partitioner)
    {
        if (arr == null || arr.length == 0)
            throw new IllegalArgumentException("Array must not be null or empty");
        if (k < 1 || k > arr.length)
            throw new IllegalArgumentException(
                "k must be between 1 and " + arr.length + ", got " + k);

        int partition = partitioner.partition(arr, low, high);

        if (partition == k - 1)
            return arr[partition];

        else if (partition < k - 1)
            return kthSmallest(arr, partition + 1, high, k, partitioner);

        else
            return kthSmallest(arr, low, partition - 1, k, partitioner);
    }
}
