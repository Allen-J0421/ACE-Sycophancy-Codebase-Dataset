package sorting;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link BubbleSort}'s allocation-free primitive {@code int[]} paths. */
class BubbleSortPrimitiveTest {

    @Test
    void sortsAscendingByDefault() {
        int[] data = {64, 34, 25, 12, 22, 11, 90};
        BubbleSort.sort(data);
        assertArrayEquals(new int[]{11, 12, 22, 25, 34, 64, 90}, data);
    }

    @Test
    void sortsWithCustomComparator() {
        int[] data = {1, 4, 2, 3};
        BubbleSort.sort(data, IntComparator.DESCENDING);
        assertArrayEquals(new int[]{4, 3, 2, 1}, data);
    }

    @Test
    void handlesEmptyAndSingle() {
        int[] empty = {};
        BubbleSort.sort(empty);
        assertArrayEquals(new int[]{}, empty);

        int[] single = {7};
        BubbleSort.sort(single);
        assertArrayEquals(new int[]{7}, single);
    }

    @Test
    void rejectsNulls() {
        assertThrows(NullPointerException.class, () -> BubbleSort.sort((int[]) null));
        assertThrows(NullPointerException.class, () -> BubbleSort.sort(new int[]{1}, null));
    }
}
