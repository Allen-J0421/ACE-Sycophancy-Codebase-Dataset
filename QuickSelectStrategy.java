import java.util.Random;

class QuickSelectStrategy implements SelectionStrategy {

    private final Random rand;

    public QuickSelectStrategy(Random rand)
    {
        this.rand = rand;
    }

    @Override
    public int select(int[] arr, int k)
    {
        return QuickSelectUtils.kthSmallest(arr, 0, arr.length - 1, k, rand);
    }
}
