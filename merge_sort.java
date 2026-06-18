import java.util.Comparator;

interface Sorter<T> {
    void sort(T[] array);
    void sort(T[] array, Comparator<T> comparator);
}

class MergeSort<T extends Comparable<T>> implements Sorter<T> {
    private final Comparator<T> comparator;

    public MergeSort() {
        this.comparator = Comparable::compareTo;
    }

    public MergeSort(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void sort(T[] array) {
        sort(array, this.comparator);
    }

    @Override
    public void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0) {
            return;
        }
        mergeSort(array, 0, array.length - 1, comparator);
    }

    private void mergeSort(T[] array, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid, comparator);
            mergeSort(array, mid + 1, right, comparator);
            merge(array, left, mid, right, comparator);
        }
    }

    private void merge(T[] array, int left, int mid, int right, Comparator<T> comparator) {
        T[] leftArray = copyRange(array, left, mid);
        T[] rightArray = copyRange(array, mid + 1, right);

        int leftIdx = 0, rightIdx = 0, currentIdx = left;

        while (leftIdx < leftArray.length && rightIdx < rightArray.length) {
            if (comparator.compare(leftArray[leftIdx], rightArray[rightIdx]) <= 0) {
                array[currentIdx++] = leftArray[leftIdx++];
            } else {
                array[currentIdx++] = rightArray[rightIdx++];
            }
        }

        while (leftIdx < leftArray.length) {
            array[currentIdx++] = leftArray[leftIdx++];
        }

        while (rightIdx < rightArray.length) {
            array[currentIdx++] = rightArray[rightIdx++];
        }
    }

    @SuppressWarnings("unchecked")
    private T[] copyRange(T[] array, int from, int to) {
        int length = to - from + 1;
        T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), length);
        System.arraycopy(array, from, result, 0, length);
        return result;
    }

    public static void main(String[] args) {
        testIntegerSorting();
        testReverseOrder();
    }

    private static void testIntegerSorting() {
        Integer[] array = {38, 27, 43, 10};
        MergeSort<Integer> sorter = new MergeSort<>();
        sorter.sort(array);
        printArray("Ascending order:", array);
    }

    private static void testReverseOrder() {
        Integer[] array = {38, 27, 43, 10};
        MergeSort<Integer> sorter = new MergeSort<Integer>(Comparator.reverseOrder());
        sorter.sort(array);
        printArray("Descending order:", array);
    }

    private static <T> void printArray(String label, T[] array) {
        System.out.print(label + " ");
        for (T value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
