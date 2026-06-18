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

        BinarySearchAssertions.expectOptionalIntEquals(3, BinarySearch.findIndex(values, 10), "int search finds existing value");
        BinarySearchAssertions.expectOptionalIntEmpty(BinarySearch.findIndex(values, 7), "int search returns empty for missing value");
        BinarySearchAssertions.expectOptionalIntEquals(0, BinarySearch.findIndex(new int[] { 1 }, 1), "int search handles single-element arrays");
        BinarySearchAssertions.expectOptionalIntEmpty(BinarySearch.findIndex(new int[0], 1), "int search handles empty arrays");
        BinarySearchAssertions.expectEquals(3, BinarySearch.binarySearch(values, 10), "int compatibility method finds existing value");
    }

    private static void testGenericSearch() {
        Integer[] values = { 2, 3, 4, 10, 40 };

        BinarySearchAssertions.expectOptionalIntEquals(3, BinarySearch.findIndex(values, 10), "generic search finds existing value");
        BinarySearchAssertions.expectOptionalIntEmpty(BinarySearch.findIndex(values, 7), "generic search returns empty for missing value");
        BinarySearchAssertions.expectOptionalIntEmpty(BinarySearch.findIndex(new Integer[0], 1), "generic search handles empty arrays");
        BinarySearchAssertions.expectEquals(3, BinarySearch.binarySearch(values, 10), "generic compatibility method finds existing value");
    }

    private static void testComparatorSearch() {
        String[] values = { "a", "bb", "ccc", "dddd" };
        Comparator<String> byLength = Comparator.comparingInt(String::length);

        BinarySearchAssertions.expectOptionalIntEquals(2, BinarySearch.findIndex(values, "zzz", byLength), "comparator search uses custom ordering");
        BinarySearchAssertions.expectOptionalIntEmpty(BinarySearch.findIndex(values, "zzzzz", byLength), "comparator search returns empty when not found");
        BinarySearchAssertions.expectEquals(2, BinarySearch.binarySearch(values, "zzz", byLength), "comparator compatibility method uses custom ordering");
    }

    private static void testNullValidation() {
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch((int[]) null, 1), "null int array is rejected");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.findIndex((int[]) null, 1), "null int array is rejected for OptionalInt API");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch((Integer[]) null, 1), "null generic array is rejected");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.findIndex((Integer[]) null, 1), "null generic array is rejected for OptionalInt API");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.binarySearch(new Integer[] { 1 }, 1, null), "null comparator is rejected");
        BinarySearchAssertions.expectThrows(NullPointerException.class, () -> BinarySearch.findIndex(new Integer[] { 1 }, 1, null), "null comparator is rejected for OptionalInt API");
    }
}
