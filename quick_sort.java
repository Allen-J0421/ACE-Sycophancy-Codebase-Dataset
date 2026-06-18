interface Sorter<T extends Comparable<T>> {
    void sort(T[] array);
}

class QuickSortImpl<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] array) {
        if (array.length > 0) {
            quickSort(array, 0, array.length - 1);
        }
    }

    private void quickSort(T[] array, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(array, low, high);
            quickSort(array, low, partitionIndex - 1);
            quickSort(array, partitionIndex + 1, high);
        }
    }

    private int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) < 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

class QuickSort {

    public static void main(String[] args) {
        Integer[] numbers = {10, 7, 8, 9, 1, 5};

        Sorter<Integer> sorter = new QuickSortImpl<>();
        sorter.sort(numbers);

        System.out.println("Sorted array: " + java.util.Arrays.toString(numbers));
    }
}
