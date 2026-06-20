package sorting;

/**
 * A comparison function for primitive {@code int} values.
 *
 * <p>The JDK's {@link java.util.Comparator} cannot order a primitive array
 * without boxing every element. This primitive-specialized variant lets
 * {@link BubbleSort} impose a custom ordering on an {@code int[]} with no
 * allocation, mirroring the comparator support already offered for object
 * arrays.
 */
@FunctionalInterface
public interface IntComparator {

    /** Natural ascending order, equivalent to {@link Integer#compare}. */
    IntComparator ASCENDING = Integer::compare;

    /** Reverse (descending) order. */
    IntComparator DESCENDING = (a, b) -> Integer.compare(b, a);

    /**
     * Compares its two arguments for order.
     *
     * @return a negative integer, zero, or a positive integer as {@code a} is
     *         less than, equal to, or greater than {@code b}
     */
    int compare(int a, int b);
}
