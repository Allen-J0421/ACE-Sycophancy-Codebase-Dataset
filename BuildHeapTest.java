import java.util.Arrays;

public final class BuildHeapTest {

    private static final BuildCase[] BUILD_CASES = {
        new BuildCase(
                "full build",
                new int[] {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17},
                new int[] {17, 15, 13, 9, 6, 5, 10, 4, 8, 3, 1},
                11),
        new BuildCase(
                "prefix build",
                new int[] {4, 10, 3, 5, 1, 99, 100},
                new int[] {10, 5, 3, 4, 1, 99, 100},
                5)
    };

    private static final HeapCheckCase[] HEAP_CHECK_CASES = {
        new HeapCheckCase("valid heap", new int[] {20, 15, 18, 9, 7, 12, 14}, true),
        new HeapCheckCase("invalid heap", new int[] {10, 12, 9}, false)
    };

    private BuildHeapTest() {
        // Test class.
    }

    public static void main(String[] args) {
        testSiftDown();
        testBuildCases();
        testHeapChecks();
        testDegenerateInputs();
        testInvalidArguments();

        System.out.println("All heap tests passed");
    }

    private static void testSiftDown() {
        int[] values = {1, 3, 2};
        int[] expected = {3, 1, 2};

        BuildHeap.siftDown(values, values.length, 0);

        assertArrayEquals(expected, values, "siftDown should restore the heap property below the root");
    }

    private static void testBuildCases() {
        for (BuildCase testCase : BUILD_CASES) {
            int[] values = Arrays.copyOf(testCase.input, testCase.input.length);

            if (testCase.buildsEntireArray()) {
                BuildHeap.buildMaxHeap(values);
                assertTrue(BuildHeap.isMaxHeap(values), testCase.name + " should satisfy the max-heap property");
            } else {
                BuildHeap.buildMaxHeap(values, testCase.heapSize);
                assertTrue(BuildHeap.isMaxHeap(values, testCase.heapSize),
                        testCase.name + " prefix should satisfy the max-heap property");
                assertSuffixUnchanged(testCase, values);
            }

            assertArrayEquals(testCase.expected, values, testCase.name + " should match the expected layout");
        }
    }

    private static void testHeapChecks() {
        for (HeapCheckCase testCase : HEAP_CHECK_CASES) {
            assertBooleanEquals(testCase.expectedResult, BuildHeap.isMaxHeap(testCase.values),
                    testCase.name + " should be classified correctly");
        }
    }

    private static void testDegenerateInputs() {
        int[] empty = {};
        int[] single = {42};

        BuildHeap.buildMaxHeap(empty);
        BuildHeap.buildMaxHeap(single);

        assertTrue(BuildHeap.isMaxHeap(empty), "Empty array is a valid heap");
        assertTrue(BuildHeap.isMaxHeap(single), "Single-element array is a valid heap");
        assertEquals(42, single[0], "Single-element array should remain unchanged");
    }

    private static void testInvalidArguments() {
        expectThrows(NullPointerException.class, () -> BuildHeap.buildMaxHeap(null));
        expectThrows(NullPointerException.class, () -> BuildHeap.isMaxHeap(null));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.buildMaxHeap(new int[] {1, 2}, 3));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.buildMaxHeap(new int[] {1, 2}, -1));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.siftDown(new int[] {1, 2}, 2, 2));
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertBooleanEquals(boolean expected, boolean actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertSuffixUnchanged(BuildCase testCase, int[] actualValues) {
        int[] expectedSuffix = Arrays.copyOfRange(testCase.input, testCase.heapSize, testCase.input.length);
        int[] actualSuffix = Arrays.copyOfRange(actualValues, testCase.heapSize, actualValues.length);

        assertArrayEquals(expectedSuffix, actualSuffix, testCase.name + " should leave the suffix untouched");
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                    "Expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName(),
                    thrown);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static final class BuildCase {
        private final String name;
        private final int[] input;
        private final int[] expected;
        private final int heapSize;

        private BuildCase(String name, int[] input, int[] expected, int heapSize) {
            this.name = name;
            this.input = input;
            this.expected = expected;
            this.heapSize = heapSize;
        }

        private boolean buildsEntireArray() {
            return heapSize == input.length;
        }
    }

    private static final class HeapCheckCase {
        private final String name;
        private final int[] values;
        private final boolean expectedResult;

        private HeapCheckCase(String name, int[] values, boolean expectedResult) {
            this.name = name;
            this.values = values;
            this.expectedResult = expectedResult;
        }
    }
}
