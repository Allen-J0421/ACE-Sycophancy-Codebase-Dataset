import java.util.Comparator;

public class SortValidator {
    public static <T> void validateBeforeSort(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
    }

    public static <T> void validateAfterSort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length <= 1) {
            return;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
                throw new IllegalStateException(
                        String.format("Array not properly sorted at index %d: %s > %s",
                                i, array[i], array[i + 1]));
            }
        }
    }

    public static <T> boolean isSorted(T[] array, Comparator<T> comparator) {
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
}
