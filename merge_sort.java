interface SortStrategy<T extends Comparable<T>> {
    void sort(T[] arr);
}

class MergeSort<T extends Comparable<T>> implements SortStrategy<T> {

    @Override
    public void sort(T[] arr) {
        mergeSort(arr, 0, arr.length - 1);
    }

    private void mergeSort(T[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    @SuppressWarnings("unchecked")
    private void merge(T[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        T[] L = (T[]) new Comparable[n1];
        T[] R = (T[]) new Comparable[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].compareTo(R[j]) <= 0) {
                arr[k] = L[i++];
            } else {
                arr[k] = R[j++];
            }
            k++;
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
}

class Sorter<T extends Comparable<T>> {
    private final SortStrategy<T> strategy;

    Sorter(SortStrategy<T> strategy) {
        this.strategy = strategy;
    }

    void sort(T[] arr) {
        strategy.sort(arr);
    }
}

class Main {
    private static <T> void printArray(T[] arr) {
        for (T x : arr)
            System.out.print(x + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        Integer[] ints = {38, 27, 43, 10};
        Sorter<Integer> intSorter = new Sorter<>(new MergeSort<Integer>());
        intSorter.sort(ints);
        printArray(ints);

        String[] words = {"banana", "apple", "cherry", "date"};
        Sorter<String> strSorter = new Sorter<>(new MergeSort<String>());
        strSorter.sort(words);
        printArray(words);
    }
}
