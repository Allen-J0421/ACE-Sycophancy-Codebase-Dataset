package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Correctness tests run against every {@link Sorter} implementation
 * polymorphically through the interface.
 */
class SorterTest {

    /** Supplies each algorithm under test, labelled by its class name. */
    static Stream<Arguments> sorters() {
        return Stream.of(new BubbleSort(), new InsertionSort(), new MergeSort(), new QuickSort())
                .map(s -> Arguments.of(Named.of(s.getClass().getSimpleName(), s)));
    }

    @ParameterizedTest(name = "{0} sorts already-sorted input")
    @MethodSource("sorters")
    void alreadySorted(Sorter sorter) {
        Integer[] data = {1, 2, 3};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{1, 2, 3}, data);
    }

    @ParameterizedTest(name = "{0} sorts reversed input")
    @MethodSource("sorters")
    void reversed(Sorter sorter) {
        Integer[] data = {3, 2, 1};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{1, 2, 3}, data);
    }

    @ParameterizedTest(name = "{0} sorts input with duplicates")
    @MethodSource("sorters")
    void duplicates(Sorter sorter) {
        Integer[] data = {2, 1, 2, 1};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{1, 1, 2, 2}, data);
    }

    @ParameterizedTest(name = "{0} handles an empty array")
    @MethodSource("sorters")
    void empty(Sorter sorter) {
        Integer[] data = {};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{}, data);
    }

    @ParameterizedTest(name = "{0} handles a single element")
    @MethodSource("sorters")
    void single(Sorter sorter) {
        Integer[] data = {42};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{42}, data);
    }

    @ParameterizedTest(name = "{0} sorts negative values")
    @MethodSource("sorters")
    void negatives(Sorter sorter) {
        Integer[] data = {0, -1, 5, -10};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{-10, -1, 0, 5}, data);
    }

    @ParameterizedTest(name = "{0} sorts the demo data")
    @MethodSource("sorters")
    void demoData(Sorter sorter) {
        Integer[] data = {64, 34, 25, 12, 22, 11, 90};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{11, 12, 22, 25, 34, 64, 90}, data);
    }

    @ParameterizedTest(name = "{0} sorts a larger shuffled array")
    @MethodSource("sorters")
    void larger(Sorter sorter) {
        Integer[] data = {9, 7, 8, 3, 1, 6, 5, 2, 4, 0};
        sorter.sort(data);
        assertArrayEquals(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, data);
    }

    @ParameterizedTest(name = "{0} honors a reverse comparator")
    @MethodSource("sorters")
    void reverseComparator(Sorter sorter) {
        Integer[] data = {1, 2, 3, 4, 5};
        sorter.sort(data, Comparator.reverseOrder());
        assertArrayEquals(new Integer[]{5, 4, 3, 2, 1}, data);
    }

    @ParameterizedTest(name = "{0} sorts any Comparable type")
    @MethodSource("sorters")
    void strings(Sorter sorter) {
        String[] data = {"banana", "apple", "cherry"};
        sorter.sort(data);
        assertArrayEquals(new String[]{"apple", "banana", "cherry"}, data);
    }

    @ParameterizedTest(name = "{0} rejects null arguments")
    @MethodSource("sorters")
    void rejectsNulls(Sorter sorter) {
        assertThrows(NullPointerException.class,
                () -> sorter.sort((Integer[]) null, Comparator.naturalOrder()));
        assertThrows(NullPointerException.class,
                () -> sorter.sort(new Integer[]{1}, null));
        assertThrows(NullPointerException.class,
                () -> sorter.sort(new Integer[]{1}, Comparator.naturalOrder(), null));
    }

    @DisplayName("stable sorts preserve the input order of equal keys")
    @ParameterizedTest(name = "{0} is stable")
    @MethodSource("stableSorters")
    void stable(Sorter sorter) {
        Item[] items = {
            new Item(1, 0), new Item(2, 1), new Item(1, 2), new Item(2, 3), new Item(1, 4)
        };
        sorter.sort(items, Comparator.comparingInt(Item::key));
        assertArrayEquals(new Item[]{
            new Item(1, 0), new Item(1, 2), new Item(1, 4), new Item(2, 1), new Item(2, 3)
        }, items);
    }

    /** Only the stable algorithms are exercised by {@link #stable}. */
    static Stream<Arguments> stableSorters() {
        return Stream.of(new BubbleSort(), new InsertionSort(), new MergeSort())
                .map(s -> Arguments.of(Named.of(s.getClass().getSimpleName(), s)));
    }

    /** A keyed element used to verify stable ordering; {@code id} breaks ties. */
    private record Item(int key, int id) {
    }
}
