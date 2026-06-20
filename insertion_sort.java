import java.util.Arrays;
import java.util.Comparator;

/**
 * Static convenience facade over a {@link Sorter}.
 *
 * <p>The sorting algorithm itself lives in {@link InsertionSorter} (behind the
 * {@link Sorter} interface); this class just exposes a no-instantiation,
 * statically-callable API and delegates to it. Swapping the algorithm is a
 * one-line change to {@link #SORTER}.
 */
public final class InsertionSort {

    /** The algorithm all static methods delegate to. */
    private static final Sorter SORTER = new InsertionSorter();

    private InsertionSort() {
        // Utility class; not meant to be instantiated.
    }

    /** Sorts an array of primitives in ascending order, in place. */
    public static void sort(int[] arr) {
        SORTER.sort(arr);
    }

    /** Sorts an array in ascending natural order, in place. */
    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        SORTER.sort(arr);
    }

    /** Sorts an array in place using the supplied comparator. */
    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        SORTER.sort(arr, comparator);
    }

    public static void main(String[] args) {
        int[] ints = { 12, 11, 13, 5, 6 };
        sort(ints);
        System.out.println(Arrays.toString(ints));

        String[] words = { "pear", "apple", "fig", "banana" };
        sort(words);
        System.out.println(Arrays.toString(words));

        Integer[] descending = { 12, 11, 13, 5, 6 };
        sort(descending, Comparator.reverseOrder());
        System.out.println(Arrays.toString(descending));
    }
}
