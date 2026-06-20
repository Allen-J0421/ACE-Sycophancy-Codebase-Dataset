package sorting;

/**
 * A {@link SortObserver} that tallies the comparisons and swaps performed during
 * a sort. Create one, pass it to
 * {@link Sorter#sort(Object[], java.util.Comparator, SortObserver)}, then read
 * the counts.
 *
 * <p>Note that the comparison-based algorithms move data differently: bubble and
 * quick sort exchange elements (counted as swaps), whereas insertion sort shifts
 * and merge sort copies into a buffer — neither swaps, so their swap count is
 * legitimately zero. Comparison counts are the metric that is comparable across
 * all algorithms.
 */
public final class SortStats implements SortObserver {

    private long comparisons;
    private long swaps;

    @Override
    public void onCompare() {
        comparisons++;
    }

    @Override
    public void onSwap() {
        swaps++;
    }

    /** The number of comparisons observed so far. */
    public long comparisons() {
        return comparisons;
    }

    /** The number of swaps observed so far. */
    public long swaps() {
        return swaps;
    }

    /** Resets both counters to zero for reuse across runs. */
    public void reset() {
        comparisons = 0;
        swaps = 0;
    }

    @Override
    public String toString() {
        return comparisons + " comparisons, " + swaps + " swaps";
    }
}
