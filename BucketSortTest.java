import java.util.Arrays;

public final class BucketSortTest {

    private BucketSortTest() {
    }

    public static void main(String[] args) {
        sortsValuesInPlace();
        returnsSortedCopyWithoutMutatingInput();
        returnsDistinctArrayForSortedCopy();
        sortsDuplicateValues();
        sortsValuesNearUpperBound();
        rejectsOutOfRangeValues();
        rejectsNullInput();
        handlesEmptyInput();
    }

    private static void sortsValuesInPlace() {
        float[] values = {0.42f, 0.32f, 0.23f, 0.52f, 0.25f, 0.47f};
        BucketSort.sort(values);

        assertArrayEquals(
            new float[]{0.23f, 0.25f, 0.32f, 0.42f, 0.47f, 0.52f},
            values,
            "sort should order values in place"
        );
    }

    private static void returnsSortedCopyWithoutMutatingInput() {
        float[] original = {0.78f, 0.17f, 0.39f};
        float[] copy = BucketSort.sortedCopy(original);

        assertArrayEquals(
            new float[]{0.78f, 0.17f, 0.39f},
            original,
            "sortedCopy should not mutate the source array"
        );
        assertArrayEquals(
            new float[]{0.17f, 0.39f, 0.78f},
            copy,
            "sortedCopy should return sorted data"
        );
    }

    private static void returnsDistinctArrayForSortedCopy() {
        float[] original = {0.31f, 0.21f};
        float[] copy = BucketSort.sortedCopy(original);

        if (copy == original) {
            throw new AssertionError("sortedCopy should return a distinct array instance");
        }
    }

    private static void sortsDuplicateValues() {
        float[] values = {0.41f, 0.12f, 0.41f, 0.12f, 0.73f};
        BucketSort.sort(values);

        assertArrayEquals(
            new float[]{0.12f, 0.12f, 0.41f, 0.41f, 0.73f},
            values,
            "sort should handle duplicate values"
        );
    }

    private static void sortsValuesNearUpperBound() {
        float[] values = {0.9999f, 0.0001f, 0.5f};
        BucketSort.sort(values);

        assertArrayEquals(
            new float[]{0.0001f, 0.5f, 0.9999f},
            values,
            "sort should support values close to the upper bound"
        );
    }

    private static void rejectsOutOfRangeValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> BucketSort.sort(new float[]{0.5f, 1.0f}),
            "sort should reject values outside [0.0, 1.0)"
        );
    }

    private static void rejectsNullInput() {
        assertThrows(
            NullPointerException.class,
            () -> BucketSort.sort(null),
            "sort should reject null input"
        );
    }

    private static void handlesEmptyInput() {
        float[] values = {};
        BucketSort.sort(values);
        assertArrayEquals(new float[]{}, values, "sort should leave empty input unchanged");
    }

    private static void assertArrayEquals(float[] expected, float[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                message
                    + ". Expected "
                    + Arrays.toString(expected)
                    + " but was "
                    + Arrays.toString(actual)
            );
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedType,
        ThrowingRunnable runnable,
        String message
    ) {
        try {
            runnable.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }
            throw new AssertionError(
                message + ". Expected " + expectedType.getSimpleName() + " but caught " + error,
                error
            );
        }

        throw new AssertionError(
            message + ". Expected " + expectedType.getSimpleName() + " but nothing was thrown"
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
