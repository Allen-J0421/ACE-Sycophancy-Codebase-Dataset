package bucketsort;

import java.util.Arrays;

public final class BucketSortTest {

    private BucketSortTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldSortInPlace();
        shouldReturnSortedCopyWithoutMutatingInput();
        shouldUseCustomBucketCount();
        shouldHandleNegativeValues();
        shouldHandleDuplicates();
        shouldHandleTrivialInputs();
        shouldRejectNonPositiveBucketCount();
        System.out.println("All BucketSort tests passed.");
    }

    private static void shouldSortInPlace() {
        float[] values = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        BucketSort.sort(values);
        assertArrayEquals(new float[]{0.1234f, 0.3434f, 0.565f, 0.656f, 0.665f, 0.897f}, values);
    }

    private static void shouldReturnSortedCopyWithoutMutatingInput() {
        float[] values = {3.5f, 1.0f, 2.0f};
        float[] copy = BucketSort.sortedCopy(values);

        assertArrayEquals(new float[]{3.5f, 1.0f, 2.0f}, values);
        assertArrayEquals(new float[]{1.0f, 2.0f, 3.5f}, copy);
    }

    private static void shouldUseCustomBucketCount() {
        float[] values = {9.0f, -1.0f, 5.0f, 2.0f, 8.0f};
        float[] copy = BucketSort.sortedCopy(values, 3);

        assertArrayEquals(new float[]{9.0f, -1.0f, 5.0f, 2.0f, 8.0f}, values);
        assertArrayEquals(new float[]{-1.0f, 2.0f, 5.0f, 8.0f, 9.0f}, copy);
    }

    private static void shouldHandleNegativeValues() {
        float[] values = {-2.0f, 4.0f, -1.5f, 0.0f, 1.5f};
        BucketSort.sort(values);
        assertArrayEquals(new float[]{-2.0f, -1.5f, 0.0f, 1.5f, 4.0f}, values);
    }

    private static void shouldHandleDuplicates() {
        float[] values = {2.0f, 1.0f, 2.0f, 1.0f};
        BucketSort.sort(values);
        assertArrayEquals(new float[]{1.0f, 1.0f, 2.0f, 2.0f}, values);
    }

    private static void shouldHandleTrivialInputs() {
        BucketSort.sort(null);

        float[] empty = {};
        BucketSort.sort(empty);
        assertArrayEquals(new float[]{}, empty);

        float[] single = {7.0f};
        BucketSort.sort(single);
        assertArrayEquals(new float[]{7.0f}, single);

        float[] flat = {5.0f, 5.0f, 5.0f};
        BucketSort.sort(flat);
        assertArrayEquals(new float[]{5.0f, 5.0f, 5.0f}, flat);
    }

    private static void shouldRejectNonPositiveBucketCount() {
        assertThrows(IllegalArgumentException.class, () -> BucketSort.sort(new float[]{1.0f, 2.0f}, 0));
        assertThrows(IllegalArgumentException.class, () -> BucketSort.sortedCopy(new float[]{1.0f, 2.0f}, -1));
    }

    private static void assertArrayEquals(float[] expected, float[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but found " + Arrays.toString(actual)
            );
        }
    }

    private static <T extends Throwable> void assertThrows(Class<T> expectedType, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(
                "Expected " + expectedType.getSimpleName() + " but caught " + thrown.getClass().getSimpleName(),
                thrown
            );
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
