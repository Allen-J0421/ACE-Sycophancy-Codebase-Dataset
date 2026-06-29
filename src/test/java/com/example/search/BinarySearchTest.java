package com.example.search;

import java.util.Objects;

final class BinarySearchTest {
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
        assertEquals(3, BinarySearch.indexOf(new int[] {2, 3, 4, 10, 40}, 10));
    }

    private static void findsTargetAtBounds() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(0, BinarySearch.indexOf(values, 2));
        assertEquals(4, BinarySearch.indexOf(values, 40));
    }

    private static void findsFirstDuplicate() {
        assertEquals(1, BinarySearch.indexOf(new int[] {2, 4, 4, 4, 10}, 4));
    }

    private static void reportsMissingTarget() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(values, 5));
        assertFalse(BinarySearch.contains(values, 5));
    }

    private static void reportsContainingTarget() {
        assertTrue(BinarySearch.contains(new int[] {2, 3, 4, 10, 40}, 10));
    }

    private static void reportsInsertionPoints() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(0, BinarySearch.insertionPoint(values, 1));
        assertEquals(3, BinarySearch.insertionPoint(values, 5));
        assertEquals(5, BinarySearch.insertionPoint(values, 50));
    }

    private static void reportsFoundResult() {
        SearchResult result = BinarySearch.search(new int[] {2, 3, 4, 10, 40}, 10);

        assertTrue(result.found());
        assertEquals(3, result.index());
        assertEquals(3, result.indexOrDefault(99));
    }

    private static void reportsMissingResult() {
        SearchResult result = BinarySearch.search(new int[] {2, 3, 4, 10, 40}, 5);

        assertFalse(result.found());
        assertEquals(99, result.indexOrDefault(99));
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
