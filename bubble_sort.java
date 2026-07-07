import java.util.Arrays;

class BubbleSort<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    private void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        Sorter<Integer> sorter = new BubbleSort<>();
        Integer[] arr = { 64, 34, 25, 12, 22, 11, 90 };
        sorter.sort(arr);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }
}
