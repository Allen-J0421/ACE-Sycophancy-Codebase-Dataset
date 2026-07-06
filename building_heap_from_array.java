public class BuildHeap {

    public static void main(String[] args)
    {

        int arr[] = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        IHeapBuilder heapUtility = new HeapUtility(arr);
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

class HeapUtility implements IHeapBuilder {

    private int[] arr;

    HeapUtility(int[] arr)
    {
        this.arr = arr;
    }

    @Override
    public int[] getArray()
    {
        return arr;
    }

    private void swap(int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private void heapify(int n, int i)
    {

        int largest = i;

        int l = 2 * i + 1;

        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[largest])
            largest = l;

        if (r < n && arr[r] > arr[largest])
            largest = r;

        if (largest != i) {
            swap(i, largest);

            heapify(n, largest);
        }
    }

    @Override
    public void buildHeap()
    {

        int n = arr.length;

        int startIdx = (n / 2) - 1;

        for (int i = startIdx; i >= 0; i--) {
            heapify(n, i);
        }
    }
}
