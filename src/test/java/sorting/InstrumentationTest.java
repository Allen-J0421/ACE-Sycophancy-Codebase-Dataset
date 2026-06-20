package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests that the {@link SortObserver} hook reports comparisons and swaps, and
 * that the counts expose real algorithmic behavior.
 */
class InstrumentationTest {

    @Test
    void bubbleEarlyExitsOnSortedInput() {
        Integer[] data = {1, 2, 3, 4, 5};
        SortStats stats = new SortStats();
        new BubbleSort().sort(data, Comparator.naturalOrder(), stats);
        assertEquals(4, stats.comparisons(), "one pass of n-1 comparisons");
        assertEquals(0, stats.swaps(), "no swaps on sorted input");
    }

    @Test
    void bubbleWorstCaseOnReversedInput() {
        Integer[] data = {5, 4, 3, 2, 1};
        SortStats stats = new SortStats();
        new BubbleSort().sort(data, Comparator.naturalOrder(), stats);
        assertEquals(10, stats.comparisons(), "n(n-1)/2 comparisons");
        assertEquals(10, stats.swaps(), "n(n-1)/2 swaps");
    }

    @Test
    void insertionBestCaseShiftsNotSwaps() {
        Integer[] data = {1, 2, 3, 4, 5};
        SortStats stats = new SortStats();
        new InsertionSort().sort(data, Comparator.naturalOrder(), stats);
        assertEquals(4, stats.comparisons(), "n-1 comparisons on sorted input");
        assertEquals(0, stats.swaps(), "insertion sort shifts rather than swaps");
    }

    @ParameterizedTest(name = "{0} reports comparisons while sorting correctly")
    @MethodSource("sorters")
    void observedSortIsCorrectAndCounted(Sorter sorter) {
        Integer[] data = {3, 1, 4, 1, 5, 9, 2, 6};
        SortStats stats = new SortStats();
        sorter.sort(data, Comparator.naturalOrder(), stats);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 4, 5, 6, 9}, data);
        assertTrue(stats.comparisons() > 0, "every comparison-based sort compares at least once");
    }

    @Test
    void noOpObserverMatchesConvenienceOverload() {
        Integer[] viaConvenience = {3, 1, 2};
        Integer[] viaNoOp = {3, 1, 2};
        new QuickSort().sort(viaConvenience, Comparator.naturalOrder());
        new QuickSort().sort(viaNoOp, Comparator.naturalOrder(), SortObserver.NO_OP);
        assertArrayEquals(viaConvenience, viaNoOp);
    }

    @Test
    void resetClearsCounts() {
        SortStats stats = new SortStats();
        new BubbleSort().sort(new Integer[]{2, 1}, Comparator.naturalOrder(), stats);
        assertTrue(stats.comparisons() > 0);
        stats.reset();
        assertEquals(0, stats.comparisons());
        assertEquals(0, stats.swaps());
    }

    static Stream<Arguments> sorters() {
        return Stream.of(new BubbleSort(), new InsertionSort(), new MergeSort(), new QuickSort())
                .map(s -> Arguments.of(Named.of(s.getClass().getSimpleName(), s)));
    }
}
