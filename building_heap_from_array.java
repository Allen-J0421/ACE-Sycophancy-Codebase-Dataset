public class BuildHeap {

    public static void main(String[] args)
    {

        int arr[] = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        IHeapBuilder heapUtility = HeapBuilderFactory.createMaxHeapBuilder(arr);
        heapUtility.buildHeap();

        int[] result = heapUtility.getArray();
        for (int i = 0; i < result.length; ++i)
            System.out.print(result[i] + " ");
        System.out.println();

    }
}

interface IHeapBuilder {
    void buildHeap();
    int[] getArray();
}

record HeapBounds(int size) {

    boolean isValidIndex(int index)
    {
        return index < size;
    }
}

record BinaryTreeIndex(int index) {

    int left()
    {
        return 2 * index + 1;
    }

    int right()
    {
        return 2 * index + 2;
    }

    int parent()
    {
        return (index - 1) / 2;
    }
}

class SwapUtility {

    private final int[] arr;

    SwapUtility(int[] arr)
    {
        this.arr = arr;
    }

    void swap(int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

@FunctionalInterface
interface HeapifyStrategy {
    void heapify(int n, int i);

    default int leftChild(int i)
    {
        return new BinaryTreeIndex(i).left();
    }

    default int rightChild(int i)
    {
        return new BinaryTreeIndex(i).right();
    }

    default int parentOf(int i)
    {
        return new BinaryTreeIndex(i).parent();
    }

    static HeapifyStrategy maxHeapify(int[] arr)
    {
        SwapUtility swapper = new SwapUtility(arr);
        HeapifyStrategy[] self = new HeapifyStrategy[1];

        self[0] = (n, i) -> {

            HeapBounds bounds = new HeapBounds(n);

            int largest = i;

            int l = self[0].leftChild(i);

            int r = self[0].rightChild(i);

            if (bounds.isValidIndex(l) && arr[l] > arr[largest])
                largest = l;

            if (bounds.isValidIndex(r) && arr[r] > arr[largest])
                largest = r;

            if (largest != i) {
                swapper.swap(i, largest);

                self[0].heapify(n, largest);
            }
        };

        return self[0];
    }
}

class HeapBuilderFactory {

    static IHeapBuilder createMaxHeapBuilder(int[] arr)
    {
        return new HeapUtility(arr, HeapifyStrategy.maxHeapify(arr));
    }
}

class HeapUtility implements IHeapBuilder {

    private final int[] arr;
    private final HeapifyStrategy strategy;

    HeapUtility(int[] arr, HeapifyStrategy strategy)
    {
        this.arr = arr;
        this.strategy = strategy;
    }

    @Override
    public int[] getArray()
    {
        return arr;
    }

    @Override
    public void buildHeap()
    {

        int n = arr.length;

        int startIdx = (n / 2) - 1;

        for (int i = startIdx; i >= 0; i--) {
            strategy.heapify(n, i);
        }
    }
}
