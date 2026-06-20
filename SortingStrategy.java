import java.util.Comparator;

/**
 * Selectable sorting strategies.
 *
 * <p>Each constant maps a stable identifier to a concrete {@link Sorter}
 * implementation and delegates to it, so callers can choose an algorithm by name
 * (e.g. {@code SortingStrategy.BUBBLE}) while still programming to the
 * {@link Sorter} interface. The held implementations are stateless singletons
 * and therefore safe to share.
 *
 * <p>Register a new algorithm by adding a constant here; no caller needs to
 * change.
 */
public enum SortingStrategy implements Sorter {

    /** Insertion sort: O(n^2) worst case, O(n) on sorted input, stable. */
    INSERTION(new InsertionSorter()),

    /** Bubble sort: O(n^2) worst case, O(n) on sorted input, stable. */
    BUBBLE(new BubbleSorter());

    private final Sorter sorter;

    SortingStrategy(Sorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public void sort(int[] arr) {
        sorter.sort(arr);
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        sorter.sort(arr, comparator);
    }
}
