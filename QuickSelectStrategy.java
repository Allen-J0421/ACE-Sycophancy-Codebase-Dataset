class QuickSelectStrategy implements SelectionStrategy {

    private final PartitionStrategy partitioner;

    public QuickSelectStrategy(PartitionStrategy partitioner)
    {
        this.partitioner = partitioner;
    }

    @Override
    public int select(int[] arr, int k)
    {
        return QuickSelectUtils.kthSmallest(arr, 0, arr.length - 1, k, partitioner);
    }
}
