import java.util.Arrays;
import java.util.List;

public final class QuickSortTest {
    private QuickSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        for (TestCase testCase : TEST_CASES) {
            runCase(testCase);
        }
    }

    private static final List<TestCase> TEST_CASES = List.of(
            new TestCase("sort whole array", QuickSortTest::shouldSortWholeArray),
            new TestCase("sort requested range", QuickSortTest::shouldSortOnlyRequestedRange),
            new TestCase("handle duplicates and empty input", QuickSortTest::shouldHandleDuplicatesAndEmptyInputs),
            new TestCase("sort many duplicates", QuickSortTest::shouldSortManyDuplicates),
            new TestCase("reject invalid ranges", QuickSortTest::shouldRejectInvalidRanges));

    private static void shouldSortWholeArray() {
        int[] values = {10, 7, 8, 9, 1, 5};
        QuickSort.sort(values);
        assertArrayEquals(new int[] {1, 5, 7, 8, 9, 10}, values);
    }

    private static void shouldSortOnlyRequestedRange() {
        int[] values = {9, 4, 3, 8, 1, 7};
        QuickSort.sort(values, 1, 5);
        assertArrayEquals(new int[] {9, 1, 3, 4, 8, 7}, values);
    }

    private static void shouldHandleDuplicatesAndEmptyInputs() {
        int[] duplicates = {3, 1, 3, 2, 3};
        QuickSort.sort(duplicates);
        assertArrayEquals(new int[] {1, 2, 3, 3, 3}, duplicates);

        int[] empty = {};
        QuickSort.sort(empty);
        assertArrayEquals(new int[] {}, empty);
    }

    private static void shouldSortManyDuplicates() {
        int[] values = {4, 2, 4, 4, 1, 2, 4, 3, 4, 2, 4};
        QuickSort.sort(values);
        assertArrayEquals(new int[] {1, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4}, values);
    }

    private static void shouldRejectInvalidRanges() {
        expectThrows(IndexOutOfBoundsException.class, () -> QuickSort.sort(new int[] {1, 2, 3}, -1, 2));
        expectThrows(IndexOutOfBoundsException.class, () -> QuickSort.sort(new int[] {1, 2, 3}, 0, 4));
        expectThrows(IndexOutOfBoundsException.class, () -> QuickSort.sort(new int[] {1, 2, 3}, 2, 1));
    }

    private static void runCase(TestCase testCase) {
        try {
            testCase.action().run();
        } catch (Throwable thrown) {
            throw new AssertionError("Case failed: " + testCase.name(), thrown);
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but found " + Arrays.toString(actual));
        }
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                    "Expected " + expectedType.getSimpleName() + " but caught " + thrown.getClass().getSimpleName(),
                    thrown);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    private record TestCase(String name, Runnable action) {}
}
