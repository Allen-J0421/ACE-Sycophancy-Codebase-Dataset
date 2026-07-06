class QuickSelectStrategy implements SelectionStrategy {

    @Override
    public int select(int[] arr, int k)
    {
        return QuickSelectUtils.kthSmallest(arr, 0, arr.length - 1, k);
    }
}
