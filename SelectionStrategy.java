import java.util.Random;

interface SelectionStrategy {
    int select(int[] arr, int k);

    static SelectionStrategy createDefault()
    {
        PartitionStrategy partitioner =
            new LoggingPartitionDecorator(new RandomPivotPartition(new Random()));
        return new QuickSelectStrategy(partitioner);
    }
}
