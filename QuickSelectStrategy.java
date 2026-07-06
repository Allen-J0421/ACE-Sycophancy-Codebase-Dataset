class QuickSelectStrategy implements SelectionStrategy {

    private final PartitionStrategy partitioner;

    public QuickSelectStrategy(PartitionStrategy partitioner)
    {
        this.partitioner = partitioner;
    }

    @Override
    public int select(int[] arr, int k)
    {
        if (arr == null || arr.length == 0)
            throw new IllegalArgumentException("Array must not be null or empty");
        if (k < 1 || k > arr.length)
            throw new IllegalArgumentException(
                "k must be between 1 and " + arr.length + ", got " + k);

        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int pivot = partitioner.partition(arr, low, high);

            if (pivot == k - 1)
                return arr[pivot];
            else if (pivot < k - 1)
                low = pivot + 1;
            else
                high = pivot - 1;
        }

        throw new IllegalStateException("Selection failed");
    }
}
