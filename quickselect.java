import java.util.Random;

class QuickSelect {

    public static void main(String[] args)
    {
        int[] array = new int[] { 10, 4, 5, 8, 6, 11, 26 };
        int[] arraycopy
            = new int[] { 10, 4, 5, 8, 6, 11, 26 };

        int kPosition = 3;
        int length = array.length;

        PartitionStrategy partitioner =
            new LoggingPartitionDecorator(new RandomPivotPartition(new Random()));
        SelectionStrategy strategy = new QuickSelectStrategy(partitioner);

        if (kPosition > length) {
            System.out.println("Index out of bound");
        }
        else {

            System.out.println(
                "K-th smallest element in array : "
                + strategy.select(arraycopy, kPosition));
        }
    }
}
