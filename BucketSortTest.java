import java.util.Arrays;

public final class BucketSortTest {

    private BucketSortTest() {
    }

    public static void main(String[] args) {
        sortsValuesInPlace();
        returnsSortedCopyWithoutMutatingInput();
        returnsDistinctArrayForSortedCopy();
        sortsDuplicateValues();
        sortsValuesAtLowerBound();
        sortsSingleValue();
        sortsValuesNearUpperBound();
        rejectsUnsupportedValues();
        rejectsNullInput();
        handlesEmptyInput();
        returnsEmptySortedCopy();
    }

    private static void sortsValuesInPlace() {
        assertSortsInPlace(
            new float[]{0.42f, 0.32f, 0.23f, 0.52f, 0.25f, 0.47f},
            new float[]{0.23f, 0.25f, 0.32f, 0.42f, 0.47f, 0.52f},
            "sort should order values in place"
        );
    }

    private static void returnsSortedCopyWithoutMutatingInput() {
        assertSortedCopy(
            new float[]{0.78f, 0.17f, 0.39f},
            new float[]{0.17f, 0.39f, 0.78f},
            "sortedCopy should not mutate the source array"
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
        assertSortsInPlace(
            new float[]{0.41f, 0.12f, 0.41f, 0.12f, 0.73f},
            new float[]{0.12f, 0.12f, 0.41f, 0.41f, 0.73f},
            "sort should handle duplicate values"
        );
    }

    private static void sortsValuesAtLowerBound() {
        assertSortsInPlace(
            new float[]{0.25f, 0.0f, 0.75f},
            new float[]{0.0f, 0.25f, 0.75f},
            "sort should support the lower bound"
        );
    }

    private static void sortsSingleValue() {
        assertSortsInPlace(
            new float[]{0.4f},
            new float[]{0.4f},
            "sort should handle a single value"
        );
    }

    private static void sortsValuesNearUpperBound() {
        assertSortsInPlace(
            new float[]{0.9999f, 0.0001f, 0.5f},
            new float[]{0.0001f, 0.5f, 0.9999f},
            "sort should support values close to the upper bound"
        );
    }

    private static void rejectsUnsupportedValues() {
        assertRejectsUnsupportedValue(1.0f, "sort should reject values outside [0.0, 1.0)");
        assertRejectsUnsupportedValue(-0.01f, "sort should reject negative values");
        assertRejectsUnsupportedValue(Float.NaN, "sort should reject NaN");
        assertRejectsUnsupportedValue(Float.NEGATIVE_INFINITY, "sort should reject negative infinity");
        assertRejectsUnsupportedValue(Float.POSITIVE_INFINITY, "sort should reject infinity");
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

    private static void returnsEmptySortedCopy() {
        float[] copy = BucketSort.sortedCopy(new float[]{});
        assertArrayEquals(new float[]{}, copy, "sortedCopy should handle empty input");
    }

    private static void assertSortsInPlace(float[] input, float[] expected, String message) {
        BucketSort.sort(input);
        assertArrayEquals(expected, input, message);
    }

    private static void assertSortedCopy(float[] input, float[] expected, String message) {
        float[] original = Arrays.copyOf(input, input.length);
        float[] copy = BucketSort.sortedCopy(input);

        assertArrayEquals(original, input, message);
        assertArrayEquals(expected, copy, "sortedCopy should return sorted data");
    }

    private static void assertRejectsUnsupportedValue(float value, String message) {
        assertThrows(
            IllegalArgumentException.class,
            () -> BucketSort.sort(new float[]{0.5f, value}),
            message
        );
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
