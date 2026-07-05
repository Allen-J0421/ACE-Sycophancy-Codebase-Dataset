import java.util.Comparator;

interface SortStrategy<T> {
    void sort(T[] arr);
}

class MergeSort<T> implements SortStrategy<T> {
    private final Comparator<T> comparator;

    MergeSort(Comparator<T> comparator) {
        this.comparator = comparator;
    }

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

        T[] L = (T[]) new Object[n1];
        T[] R = (T[]) new Object[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (comparator.compare(L[i], R[j]) <= 0) {
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

class Sorter<T> {
    private final SortStrategy<T> strategy;

    private Sorter(Builder<T> builder) {
        this.strategy = builder.strategy;
    }

    void sort(T[] arr) {
        strategy.sort(arr);
    }

    static <T> Builder<T> builder() {
        return new Builder<>();
    }

    static class Builder<T> {
        private SortStrategy<T> strategy;

        Builder<T> strategy(SortStrategy<T> strategy) {
            this.strategy = strategy;
            return this;
        }

        Builder<T> comparator(Comparator<T> comparator) {
            this.strategy = new MergeSort<>(comparator);
            return this;
        }

        Sorter<T> build() {
            if (strategy == null)
                throw new IllegalStateException("A sort strategy must be provided");
            return new Sorter<>(this);
        }
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
        Sorter.<Integer>builder()
            .comparator(Comparator.naturalOrder())
            .build()
            .sort(ints);
        printArray(ints);

        Integer[] intsDesc = {38, 27, 43, 10};
        Sorter.<Integer>builder()
            .comparator(Comparator.reverseOrder())
            .build()
            .sort(intsDesc);
        printArray(intsDesc);

        String[] words = {"Banana", "apple", "Cherry", "date"};
        Sorter.<String>builder()
            .comparator(String.CASE_INSENSITIVE_ORDER)
            .build()
            .sort(words);
        printArray(words);
    }
}
