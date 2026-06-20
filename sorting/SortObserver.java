package sorting;

/**
 * A hook notified of the primitive operations a {@link Sorter} performs:
 * comparisons and element swaps. Both methods default to no-ops, so a custom
 * observer (a tracer, a counter, a visualizer) need only override what it cares
 * about.
 *
 * <p>Instrumentation covers the generic {@link Sorter} path. The primitive
 * {@code int[]} paths on {@link BubbleSort} are intentionally left
 * uninstrumented to preserve their allocation-free hot loop.
 *
 * @see SortStats
 */
public interface SortObserver {

    /** An observer that ignores every event. Used by the uninstrumented path. */
    SortObserver NO_OP = new SortObserver() {
    };

    /** Called once for each comparison the algorithm performs. */
    default void onCompare() {
    }

    /** Called once for each pair of elements the algorithm swaps. */
    default void onSwap() {
    }
}
