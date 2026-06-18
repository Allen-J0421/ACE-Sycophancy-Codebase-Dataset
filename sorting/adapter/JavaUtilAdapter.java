package sorting.adapter;

import java.util.Comparator;

public final class JavaUtilAdapter {
    private JavaUtilAdapter() {
    }

    public interface JavaUtilSort<T extends Comparable<T>> {
        void sort(T[] array);
        void sort(T[] array, Comparator<T> comparator);
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<T>> boolean isSorted(T[] array, Comparator<T> comparator) {
        if (array == null || array.length <= 1) {
            return true;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<T>> int binarySearch(T[] array, T key) {
        return binarySearch(array, 0, array.length, key, Comparable::compareTo);
    }

    public static <T extends Comparable<T>> int binarySearch(
            T[] array, int fromIndex, int toIndex, T key, Comparator<T> comparator) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparator.compare(array[mid], key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static <T extends Comparable<T>> void shuffle(T[] array) {
        java.util.Random random = new java.util.Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
