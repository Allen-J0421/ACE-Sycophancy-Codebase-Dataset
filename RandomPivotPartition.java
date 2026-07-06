import java.util.Random;

class RandomPivotPartition implements PartitionStrategy {

    private final Random rand;

    public RandomPivotPartition(Random rand)
    {
        this.rand = rand;
    }

    private static void swap(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    @Override
    public int partition(int[] arr, int low, int high)
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
}
