public class ActivitySelectionTest {
    public static void main(String[] args) {
        selectsExpectedNumberOfCompatibleActivities();
        returnsZeroForEmptyInput();
        rejectsMismatchedInputLengths();
        rejectsNullStartInput();
        rejectsNullFinishInput();
        preservesStrictCompatibilityRuleWhenActivitiesTouch();
        aliasMethodMatchesPrimaryMethod();
    }

    private static void selectsExpectedNumberOfCompatibleActivities() {
        assertSelectionCount(
            new SelectionCase(
                4,
                new int[] {1, 3, 0, 5, 8, 5},
                new int[] {2, 4, 6, 7, 9, 9},
                "selects the expected number of compatible activities"
            )
        );
    }

    private static void returnsZeroForEmptyInput() {
        assertSelectionCount(
            new SelectionCase(
                0,
                new int[] {},
                new int[] {},
                "returns zero for empty input"
            )
        );
    }

    private static void rejectsMismatchedInputLengths() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ActivitySelection.activitySelection(new int[] {1, 2}, new int[] {3}),
            "rejects mismatched input lengths"
        );
    }

    private static void rejectsNullStartInput() {
        assertThrows(
            NullPointerException.class,
            () -> ActivitySelection.activitySelection(null, new int[] {}),
            "rejects null start input"
        );
    }

    private static void rejectsNullFinishInput() {
        assertThrows(
            NullPointerException.class,
            () -> ActivitySelection.activitySelection(new int[] {}, null),
            "rejects null finish input"
        );
    }

    private static void preservesStrictCompatibilityRuleWhenActivitiesTouch() {
        assertSelectionCount(
            new SelectionCase(
                1,
                new int[] {1, 2},
                new int[] {2, 3},
                "preserves the strict compatibility rule when activities touch"
            )
        );
    }

    private static void aliasMethodMatchesPrimaryMethod() {
        SelectionCase testCase =
            new SelectionCase(
                2,
                new int[] {1, 3, 4},
                new int[] {2, 5, 6},
                "keeps the alias method aligned with the primary method"
            );

        assertEquals(
            ActivitySelection.maximumCompatibleActivityCount(
                testCase.startTimes(),
                testCase.finishTimes()
            ),
            ActivitySelection.activitySelection(testCase.startTimes(), testCase.finishTimes()),
            testCase.message()
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
        String message
    ) {
    }
}
