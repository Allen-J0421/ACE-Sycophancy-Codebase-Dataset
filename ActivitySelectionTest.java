import java.util.Arrays;
import java.util.List;

public final class ActivitySelectionTest {

    private ActivitySelectionTest() {
        // Test utility.
    }

    public static void main(String[] args) {
        selectsExpectedActivitiesFromParallelArrays();
        selectsExpectedActivitiesFromList();
        rejectsMismatchedParallelArrays();
        rejectsInvalidActivityIntervals();
        rejectsNullActivityListEntries();
        handlesEmptyInput();
        System.out.println("All ActivitySelection tests passed.");
    }

    private static void selectsExpectedActivitiesFromParallelArrays() {
        ActivitySelection.SelectionResult result = ActivitySelection.selectMaximumNonOverlappingActivities(
            new int[] {1, 3, 0, 5, 8, 5},
            new int[] {2, 4, 6, 7, 9, 9}
        );

        assertEquals(4, result.count(), "parallel-array count");
        assertEquals(
            List.of(
                new ActivitySelection.ActivityInterval(1, 2),
                new ActivitySelection.ActivityInterval(3, 4),
                new ActivitySelection.ActivityInterval(5, 7),
                new ActivitySelection.ActivityInterval(8, 9)
            ),
            result.selectedActivities(),
            "parallel-array schedule"
        );
    }

    private static void selectsExpectedActivitiesFromList() {
        ActivitySelection.SelectionResult result = ActivitySelection.selectMaximumNonOverlappingActivities(
            List.of(
                new ActivitySelection.ActivityInterval(5, 7),
                new ActivitySelection.ActivityInterval(1, 2),
                new ActivitySelection.ActivityInterval(8, 9),
                new ActivitySelection.ActivityInterval(0, 6),
                new ActivitySelection.ActivityInterval(3, 4),
                new ActivitySelection.ActivityInterval(5, 9)
            )
        );

        assertEquals(4, result.count(), "list count");
    }

    private static void rejectsMismatchedParallelArrays() {
        expectThrows(
            IllegalArgumentException.class,
            () -> ActivitySelection.selectMaximumNonOverlappingActivities(
                new int[] {1, 2},
                new int[] {3}
            ),
            "mismatched arrays"
        );
    }

    private static void rejectsInvalidActivityIntervals() {
        expectThrows(
            IllegalArgumentException.class,
            () -> new ActivitySelection.ActivityInterval(5, 4),
            "invalid interval"
        );
    }

    private static void rejectsNullActivityListEntries() {
        expectThrows(
            NullPointerException.class,
            () -> ActivitySelection.selectMaximumNonOverlappingActivities(
                Arrays.asList(new ActivitySelection.ActivityInterval(1, 2), null)
            ),
            "null list entry"
        );
    }

    private static void handlesEmptyInput() {
        ActivitySelection.SelectionResult result = ActivitySelection.selectMaximumNonOverlappingActivities(
            List.of()
        );

        assertEquals(0, result.count(), "empty count");
        assertEquals(List.of(), result.selectedActivities(), "empty schedule");
        if (!result.isEmpty()) {
            throw new AssertionError("empty schedule should report empty");
        }
    }

    private static void assertEquals(Object expected, Object actual, String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                description + " mismatch: expected=" + expected + ", actual=" + actual
            );
        }
    }

    private static void expectThrows(
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action,
        String description
    ) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(
                description + " threw " + thrown.getClass().getSimpleName()
                    + " instead of " + expectedType.getSimpleName(),
                thrown
            );
        }

        throw new AssertionError(description + " did not throw " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
