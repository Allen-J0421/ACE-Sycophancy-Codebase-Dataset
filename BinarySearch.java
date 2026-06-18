import java.util.Comparator;
import java.util.Objects;

public final class BinarySearch {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
        // Utility class.
    }

    public static int binarySearch(int[] array, int target) {
        Objects.requireNonNull(array, "array");
        return binarySearch(array, target, 0, array.length - 1);
    }

    private static int binarySearch(int[] array, int target, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = array[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        return binarySearch(array, target, Comparator.naturalOrder());
    }

    public static <T> int binarySearch(T[] array, T target, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        return binarySearch(array, target, comparator, 0, array.length - 1);
    }

    private static <T> int binarySearch(T[] array, T target, Comparator<? super T> comparator, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            T value = array[mid];
            int comparison = comparator.compare(value, target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);
        System.out.println(formatResult(result));
    }

    private static String formatResult(int index) {
        if (index == -1) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
