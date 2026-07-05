interface Sorter {
    <T extends Comparable<T>> void sort(T[] arr);
}

class QuickSorter implements Sorter {

    @Override
    public <T extends Comparable<T>> void sort(T[] arr) {
        if (arr != null && arr.length > 1) {
            quickSort(arr, 0, arr.length - 1);
        }
    }

    private <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        T pivot = arr[high];
        int i = low - 1;
        for (int j = low; j <= high - 1; j++) {
            if (arr[j].compareTo(pivot) < 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

class QuickSort {

    public static void main(String[] args) {
        Sorter sorter = new QuickSorter();

        Integer[] ints = {10, 7, 8, 9, 1, 5};
        sorter.sort(ints);
        for (int val : ints) {
            System.out.print(val + " ");
        }
        System.out.println();

        String[] words = {"banana", "apple", "cherry", "date"};
        sorter.sort(words);
        for (String w : words) {
            System.out.print(w + " ");
        }
        System.out.println();
    }
}
