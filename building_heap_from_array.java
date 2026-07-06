public class BuildHeap {

    public static void main(String[] args)
    {

        int arr[] = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        IHeapBuilder heapUtility = new HeapUtility(arr, HeapifyStrategy.maxHeapify(arr));
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

class BinaryTreeIndex {

    private final int index;

    BinaryTreeIndex(int index)
    {
        this.index = index;
    }

    int get()
    {
        return index;
    }

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

@FunctionalInterface
interface HeapifyStrategy {
    void heapify(int n, int i);

    static HeapifyStrategy maxHeapify(int[] arr)
    {
        return new HeapifyStrategy() {

            private void swap(int i, int j)
            {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }

            @Override
            public void heapify(int n, int i)
            {

                BinaryTreeIndex node = new BinaryTreeIndex(i);

                int largest = node.get();

                int l = node.left();

                int r = node.right();

                if (l < n && arr[l] > arr[largest])
                    largest = l;

                if (r < n && arr[r] > arr[largest])
                    largest = r;

                if (largest != node.get()) {
                    swap(node.get(), largest);

                    heapify(n, largest);
                }
            }
        };
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
