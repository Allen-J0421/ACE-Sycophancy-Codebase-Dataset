import java.util.Arrays;
import java.util.Comparator;

/**
 * Static convenience facade over the {@link Sorter} strategies.
 *
 * <p>Acts as a generic entry point for sorting: every method comes in two forms,
 * one that takes an explicit {@link SortingStrategy} and one that omits it and
 * uses {@link #DEFAULT} ({@link SortingStrategy#INSERTION}). The algorithms
 * themselves live in the {@link Sorter} implementations; this class just exposes
 * a no-instantiation, statically-callable API.
 */
public final class SorterFacade {

    /** Strategy used by the overloads that don't take one explicitly. */
    public static final SortingStrategy DEFAULT = SortingStrategy.INSERTION;

    private SorterFacade() {
        // Utility class; not meant to be instantiated.
    }

    /** Sorts an array of primitives in ascending order using the default strategy. */
    public static void sort(int[] arr) {
        DEFAULT.sort(arr);
    }

    /** Sorts an array of primitives in ascending order using the given strategy. */
    public static void sort(int[] arr, SortingStrategy strategy) {
        strategy.sort(arr);
    }

    /** Sorts an array in natural order using the default strategy. */
    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        DEFAULT.sort(arr);
    }

    /** Sorts an array in natural order using the given strategy. */
    public static <T extends Comparable<? super T>> void sort(T[] arr, SortingStrategy strategy) {
        strategy.sort(arr);
    }

    /** Sorts an array with the given comparator using the default strategy. */
    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        DEFAULT.sort(arr, comparator);
    }

    /** Sorts an array with the given comparator using the given strategy. */
    public static <T> void sort(T[] arr, SortingStrategy strategy, Comparator<? super T> comparator) {
        strategy.sort(arr, comparator);
    }

    public static void main(String[] args) {
        int[] ints = { 12, 11, 13, 5, 6 };
        sort(ints);                          // default (insertion)
        System.out.println(Arrays.toString(ints));

        int[] more = { 12, 11, 13, 5, 6 };
        sort(more, SortingStrategy.BUBBLE);  // explicit strategy
        System.out.println(Arrays.toString(more));

        String[] words = { "pear", "apple", "fig", "banana" };
        sort(words);
        System.out.println(Arrays.toString(words));

        Integer[] descending = { 12, 11, 13, 5, 6 };
        sort(descending, SortingStrategy.BUBBLE, Comparator.reverseOrder());
        System.out.println(Arrays.toString(descending));
    }
}
