package binarysearch;

import java.util.Comparator;
import java.util.OptionalInt;
import java.util.function.IntUnaryOperator;
import java.util.Objects;

public final class BinarySearch {
    private BinarySearch() {
        // Utility class.
    }

    public static int binarySearch(int[] array, int target) {
        return findIndex(array, target).orElse(-1);
    }

    public static OptionalInt findIndex(int[] array, int target) {
        Objects.requireNonNull(array, "array");
        return search(array.length - 1, mid -> Integer.compare(array[mid], target));
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] array, T target) {
        return findIndex(array, target).orElse(-1);
    }

    public static <T extends Comparable<? super T>> OptionalInt findIndex(T[] array, T target) {
        return findIndex(array, target, Comparator.naturalOrder());
    }

    public static <T> int binarySearch(T[] array, T target, Comparator<? super T> comparator) {
        return findIndex(array, target, comparator).orElse(-1);
    }

    public static <T> OptionalInt findIndex(T[] array, T target, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array");
        Objects.requireNonNull(comparator, "comparator");
        return search(array.length - 1, mid -> comparator.compare(array[mid], target));
    }

    private static OptionalInt search(int right, IntUnaryOperator comparisonAtIndex) {
        int left = 0;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int comparison = comparisonAtIndex.applyAsInt(mid);

            if (comparison == 0) {
                return OptionalInt.of(mid);
            }

            if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return OptionalInt.empty();
    }
}
