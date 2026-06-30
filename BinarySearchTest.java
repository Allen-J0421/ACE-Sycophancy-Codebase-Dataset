import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BinarySearchTest {
    @Test
    void returnsFoundResultWhenIntegerElementIsPresent() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        SearchResult result = SearchUtils.binarySearch(arr, 10);

        assertEquals(SearchResult.found(3), result);
    }

    @Test
    void returnsNotFoundResultWithInsertionPointWhenIntegerElementIsAbsent() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        SearchResult result = SearchUtils.binarySearch(arr, 5);

        assertEquals(SearchResult.notFound(3), result);
    }

    @Test
    void supportsComparableTypesOtherThanInteger() {
        String[] arr = { "apple", "banana", "cherry", "date" };

        SearchResult result = SearchUtils.binarySearch(arr, "cherry");

        assertEquals(SearchResult.found(2), result);
    }

    @Test
    void returnsFoundResultWhenElementIsInsideRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        SearchResult result = SearchUtils.binarySearch(arr, 10, 2, 4);

        assertEquals(SearchResult.found(3), result);
    }

    @Test
    void returnsNotFoundResultWhenElementIsOutsideRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        SearchResult result = SearchUtils.binarySearch(arr, 10, 0, 3);

        assertEquals(SearchResult.notFound(3), result);
    }

    @Test
    void rejectsInvalidRange() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        assertThrows(IllegalArgumentException.class, () -> SearchUtils.binarySearch(arr, 10, 4, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> SearchUtils.binarySearch(arr, 10, -1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> SearchUtils.binarySearch(arr, 10, 0, 6));
    }

    @Test
    void exposesIndexOnlyConvenienceMethod() {
        Integer[] arr = { 2, 3, 4, 10, 40 };

        int result = SearchUtils.binarySearchIndex(arr, 10);

        assertEquals(3, result);
    }

    @Test
    void resultOrReturnsIndexOrInsertionPoint() {
        assertEquals(3, SearchResult.found(3).or(-1));
        assertEquals(3, SearchResult.notFound(3).or(-1));
    }
}
