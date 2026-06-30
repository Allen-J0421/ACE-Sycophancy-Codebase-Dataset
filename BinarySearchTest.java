import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
