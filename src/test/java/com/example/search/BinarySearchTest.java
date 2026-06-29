package com.example.search;

import java.util.Objects;

final class BinarySearchTest {
    private static final int[] SORTED_VALUES = {2, 3, 4, 10, 40};
    private static final int DEFAULT_INDEX = 99;

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        findsTargetInMiddle();
        findsTargetAtBounds();
        findsFirstDuplicate();
        reportsMissingTarget();
        reportsContainingTarget();
        reportsInsertionPoints();
        reportsFoundResult();
        reportsMissingResult();
        handlesEmptyArray();
        rejectsNullArray();

        System.out.println("All BinarySearch tests passed");
    }

    private static void findsTargetInMiddle() {
        assertEquals(3, BinarySearch.indexOf(SORTED_VALUES, 10));
    }

    private static void findsTargetAtBounds() {
        assertEquals(0, BinarySearch.indexOf(SORTED_VALUES, 2));
        assertEquals(4, BinarySearch.indexOf(SORTED_VALUES, 40));
    }

    private static void findsFirstDuplicate() {
        assertEquals(1, BinarySearch.indexOf(new int[] {2, 4, 4, 4, 10}, 4));
    }

    private static void reportsMissingTarget() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(SORTED_VALUES, 5));
        assertFalse(BinarySearch.contains(SORTED_VALUES, 5));
    }

    private static void reportsContainingTarget() {
        assertTrue(BinarySearch.contains(SORTED_VALUES, 10));
    }

    private static void reportsInsertionPoints() {
        assertEquals(0, BinarySearch.insertionPoint(SORTED_VALUES, 1));
        assertEquals(3, BinarySearch.insertionPoint(SORTED_VALUES, 5));
        assertEquals(5, BinarySearch.insertionPoint(SORTED_VALUES, 50));
    }

    private static void reportsFoundResult() {
        SearchResult result = BinarySearch.search(SORTED_VALUES, 10);

        assertTrue(result.found());
        assertEquals(3, result.index());
        assertEquals(3, result.indexOrDefault(DEFAULT_INDEX));
    }

    private static void reportsMissingResult() {
        SearchResult result = BinarySearch.search(SORTED_VALUES, 5);

        assertFalse(result.found());
        assertEquals(DEFAULT_INDEX, result.indexOrDefault(DEFAULT_INDEX));
        assertThrows("Search target was not found", new ThrowingRunnable() {
            @Override
            public void run() {
                result.index();
            }
        });
    }

    private static void handlesEmptyArray() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(new int[0], 10));
    }

    private static void rejectsNullArray() {
        try {
            BinarySearch.indexOf(null, 10);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("sortedArray", expected.getMessage());
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected condition to be false");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private static void assertThrows(String expectedMessage, ThrowingRunnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals(expectedMessage, expected.getMessage());
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
