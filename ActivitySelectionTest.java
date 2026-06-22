import java.util.Arrays;

public class ActivitySelectionTest {
    public static void main(String[] args) {
        SelectionCase[] selectionCases = {
            new SelectionCase(
                4,
                new int[] {1, 3, 0, 5, 8, 5},
                new int[] {2, 4, 6, 7, 9, 9},
                new int[][] {{1, 2}, {3, 4}, {5, 7}, {8, 9}},
                "selects the expected number of compatible activities"
            ),
            new SelectionCase(
                0,
                new int[] {},
                new int[] {},
                new int[][] {},
                "returns zero for empty input"
            ),
            new SelectionCase(
                1,
                new int[] {1, 2},
                new int[] {2, 3},
                new int[][] {{1, 2}},
                "preserves the strict compatibility rule when activities touch"
            )
        };

        for (SelectionCase testCase : selectionCases) {
            assertSelectionCount(testCase);
            assertSelectedActivities(testCase);
            assertSelectedActivitySlots(testCase);
            assertSelectionResult(testCase);
            assertLegacyAlias(testCase);
        }

        assertThrows(
            IllegalArgumentException.class,
            () -> ActivitySelection.activitySelection(new int[] {1, 2}, new int[] {3}),
            "rejects mismatched input lengths"
        );

        assertThrows(
            NullPointerException.class,
            () -> ActivitySelection.activitySelection(null, new int[] {}),
            "rejects null start input"
        );

        assertThrows(
            NullPointerException.class,
            () -> ActivitySelection.activitySelection(new int[] {}, null),
            "rejects null finish input"
        );
    }

    private static void assertLegacyAlias(SelectionCase testCase) {
        assertEquals(
            ActivitySelection.maximumCompatibleActivityCount(
                testCase.startTimes(),
                testCase.finishTimes()
            ),
            ActivitySelection.activitySelection(testCase.startTimes(), testCase.finishTimes()),
            testCase.message() + " through the legacy alias"
        );
    }

    private static void assertSelectionResult(SelectionCase testCase) {
        ActivitySelection.SelectionResult result =
            ActivitySelection.buildSelectionResult(testCase.startTimes(), testCase.finishTimes());

        assertEquals(
            testCase.expectedCount(),
            result.count(),
            testCase.message() + " with the expected result count"
        );
        assertDeepEquals(
            testCase.expectedActivities(),
            result.activities(),
            testCase.message() + " with the expected result activities"
        );
    }

    private static void assertSelectedActivitySlots(SelectionCase testCase) {
        ActivitySelection.SelectionResult result =
            ActivitySelection.buildSelectionResult(testCase.startTimes(), testCase.finishTimes());

        ActivitySelection.ActivitySlot[] slots = result.selectedActivities();
        int[][] actualActivities = new int[slots.length][2];
        for (int i = 0; i < slots.length; i++) {
            actualActivities[i][0] = slots[i].startTime();
            actualActivities[i][1] = slots[i].finishTime();
        }

        assertDeepEquals(
            testCase.expectedActivities(),
            actualActivities,
            testCase.message() + " with the expected slot objects"
        );
    }

    private static void assertSelectedActivities(SelectionCase testCase) {
        assertDeepEquals(
            testCase.expectedActivities(),
            ActivitySelection.selectMaximumCompatibleActivities(
                testCase.startTimes(),
                testCase.finishTimes()
            ),
            testCase.message() + " with the expected chosen activities"
        );
    }

    private static void assertSelectionCount(SelectionCase testCase) {
        assertEquals(
            testCase.expectedCount(),
            ActivitySelection.maximumCompatibleActivityCount(
                testCase.startTimes(),
                testCase.finishTimes()
            ),
            testCase.message()
        );
    }

    private static void assertDeepEquals(int[][] expected, int[][] actual, String message) {
        if (!Arrays.deepEquals(expected, actual)) {
            throw new AssertionError(
                message
                    + ": expected "
                    + Arrays.deepToString(expected)
                    + " but got "
                    + Arrays.deepToString(actual)
            );
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expected,
        ThrowingRunnable runnable,
        String message
    ) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                message + ": expected " + expected.getSimpleName() + " but got " + actual.getClass().getSimpleName(),
                actual
            );
        }

        throw new AssertionError(message + ": expected " + expected.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private record SelectionCase(
        int expectedCount,
        int[] startTimes,
        int[] finishTimes,
        int[][] expectedActivities,
        String message
    ) {
    }
}
