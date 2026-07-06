import java.util.Arrays;

class LoggingPartitionDecorator implements PartitionStrategy {

    private final PartitionStrategy wrapped;

    public LoggingPartitionDecorator(PartitionStrategy wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public int partition(int[] arr, int low, int high)
    {
        System.out.println("Partitioning " + Arrays.toString(Arrays.copyOfRange(arr, low, high + 1))
            + " [low=" + low + ", high=" + high + "]");

        int pivotloc = wrapped.partition(arr, low, high);

        System.out.println("Pivot placed at index " + pivotloc
            + ", result: " + Arrays.toString(Arrays.copyOfRange(arr, low, high + 1)));

        return pivotloc;
    }
}
