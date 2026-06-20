import java.util.Arrays;

public final class BucketSortTest {

    private BucketSortTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldSortInPlace();
        shouldReturnSortedCopyWithoutMutatingInput();
        shouldHandleNegativeValues();
        shouldHandleDuplicates();
        shouldHandleTrivialInputs();
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

    private static void assertArrayEquals(float[] expected, float[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but found " + Arrays.toString(actual)
            );
        }
    }
}
