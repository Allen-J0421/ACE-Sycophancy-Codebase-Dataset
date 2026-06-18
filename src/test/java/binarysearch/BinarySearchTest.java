package binarysearch;

import java.util.Comparator;

public final class BinarySearchTest {
    private BinarySearchTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        testIntSearch();
        testGenericSearch();
        testComparatorSearch();
        testNullValidation();
        System.out.println("BinarySearchTest passed");
    }

    private static void testIntSearch() {
        int[] values = { 2, 3, 4, 10, 40 };

        BinarySearchAssertions.expectEquals(3, BinarySearch.binarySearch(values, 10), "int search finds existing value");
        BinarySearchAssertions.expectEquals(-1, BinarySearch.binarySearch(values, 7), "int search returns -1 for missing value");
        BinarySearchAssertions.expectEquals(0, BinarySearch.binarySearch(new int[] { 1 }, 1), "int search handles single-element arrays");
        BinarySearchAssertions.expectEquals(-1, BinarySearch.binarySearch(new int[0], 1), "int search handles empty arrays");
    }

    private static void testGenericSearch() {
        Integer[] values = { 2, 3, 4, 10, 40 };

        BinarySearchAssertions.expectEquals(3, BinarySearch.binarySearch(values, 10), "generic search finds existing value");
        BinarySearchAssertions.expectEquals(-1, BinarySearch.binarySearch(values, 7), "generic search returns -1 for missing value");
        BinarySearchAssertions.expectEquals(-1, BinarySearch.binarySearch(new Integer[0], 1), "generic search handles empty arrays");
    }

    private static void testComparatorSearch() {
        String[] values = { "a", "bb", "ccc", "dddd" };
        Comparator<String> byLength = Comparator.comparingInt(String::length);

        BinarySearchAssertions.expectEquals(2, BinarySearch.binarySearch(values, "zzz", byLength), "comparator search uses custom ordering");
        BinarySearchAssertions.expectEquals(-1, BinarySearch.binarySearch(values, "zzzzz", byLength), "comparator search returns -1 when not found");
    }

    private static void testNullValidation() {
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch((int[]) null, 1), "null int array is rejected");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch((Integer[]) null, 1), "null generic array is rejected");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch(new Integer[] { 1 }, 1, null), "null comparator is rejected");
    }
}
