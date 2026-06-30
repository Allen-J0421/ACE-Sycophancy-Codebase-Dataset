import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BinarySearchTest {
    @Test
    void returnsIndexWhenIntegerElementIsPresent() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        int result = SearchUtils.binarySearch(arr, 10);

        assertEquals(3, result);
    }

    @Test
    void returnsNegativeOneWhenIntegerElementIsAbsent() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        int result = SearchUtils.binarySearch(arr, 5);

        assertEquals(-1, result);
    }

    @Test
    void supportsComparableTypesOtherThanInteger() {
        String[] arr = { "apple", "banana", "cherry", "date" };

        int result = SearchUtils.binarySearch(arr, "cherry");

        assertEquals(2, result);
    }

    @Test
    void returnsIndexWhenElementIsInsideRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        int result = SearchUtils.binarySearch(arr, 10, 2, 4);

        assertEquals(3, result);
    }

    @Test
    void returnsNegativeOneWhenElementIsOutsideRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        int result = SearchUtils.binarySearch(arr, 10, 0, 3);

        assertEquals(-1, result);
    }

    @Test
    void rejectsInvalidRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        assertThrows(IllegalArgumentException.class, () -> SearchUtils.binarySearch(arr, 10, 4, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> SearchUtils.binarySearch(arr, 10, -1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> SearchUtils.binarySearch(arr, 10, 0, 6));
    }
}
