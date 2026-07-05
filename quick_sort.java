import java.util.ArrayDeque;
import java.util.Deque;

class Sorting {

    interface Sorter {
        <T extends Comparable<T>> void sort(T[] arr);
    }

    interface PivotSelector {
        <T extends Comparable<T>> int selectPivot(T[] arr, int low, int high);
    }

    static Sorter quickSorter(PivotSelector selector) { return new QuickSorterImpl(selector); }

    static PivotSelector lastPivot()   { return new LastPivotSelector(); }
    static PivotSelector firstPivot()  { return new FirstPivotSelector(); }
    static PivotSelector randomPivot() { return new RandomPivotSelector(); }

    private static class QuickSorterImpl implements Sorter {

        private final PivotSelector pivotSelector;

        QuickSorterImpl(PivotSelector pivotSelector) {
            this.pivotSelector = pivotSelector;
        }

        @Override
        public <T extends Comparable<T>> void sort(T[] arr) {
            if (arr != null && arr.length > 1) {
                quickSort(arr, 0, arr.length - 1);
            }
        }

        private <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
            Deque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[]{low, high});
            while (!stack.isEmpty()) {
                int[] range = stack.pop();
                int lo = range[0], hi = range[1];
                if (lo >= hi) continue;
                int pi = partition(arr, lo, hi);
                // push larger partition first so the smaller is processed first,
                // keeping the deque depth bounded to O(log n) on average
                if (pi - 1 - lo > hi - (pi + 1)) {
                    stack.push(new int[]{lo, pi - 1});
                    stack.push(new int[]{pi + 1, hi});
                } else {
                    stack.push(new int[]{pi + 1, hi});
                    stack.push(new int[]{lo, pi - 1});
                }
            }
        }

        private <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
            int pivotIndex = pivotSelector.selectPivot(arr, low, high);
            swap(arr, pivotIndex, high);
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

        private static <T> void swap(T[] arr, int i, int j) {
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    private static class LastPivotSelector implements PivotSelector {
        @Override
        public <T extends Comparable<T>> int selectPivot(T[] arr, int low, int high) {
            return high;
        }
    }

    private static class FirstPivotSelector implements PivotSelector {
        @Override
        public <T extends Comparable<T>> int selectPivot(T[] arr, int low, int high) {
            return low;
        }
    }

    private static class RandomPivotSelector implements PivotSelector {
        @Override
        public <T extends Comparable<T>> int selectPivot(T[] arr, int low, int high) {
            return low + (int) (Math.random() * (high - low + 1));
        }
    }
}

class QuickSort {

    public static void main(String[] args) {
        Integer[] ints = {10, 7, 8, 9, 1, 5};
        Sorting.quickSorter(Sorting.lastPivot()).sort(ints);
        for (int val : ints) System.out.print(val + " ");
        System.out.println();

        String[] words = {"banana", "apple", "cherry", "date"};
        Sorting.quickSorter(Sorting.firstPivot()).sort(words);
        for (String w : words) System.out.print(w + " ");
        System.out.println();

        Integer[] more = {3, 6, 8, 10, 1, 2, 1};
        Sorting.quickSorter(Sorting.randomPivot()).sort(more);
        for (int val : more) System.out.print(val + " ");
        System.out.println();
    }
}
