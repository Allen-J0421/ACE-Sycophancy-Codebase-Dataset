package bucketsort;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BucketSortTest {

    @Test
    void sortsInPlace() {
        float[] values = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};

        BucketSort.sort(values);

        assertArrayEquals(new float[]{0.1234f, 0.3434f, 0.565f, 0.656f, 0.665f, 0.897f}, values, 0.0f);
    }

    @Test
    void returnsSortedCopyWithoutMutatingInput() {
        float[] values = {3.5f, 1.0f, 2.0f};

        float[] copy = BucketSort.sortedCopy(values);

        assertArrayEquals(new float[]{3.5f, 1.0f, 2.0f}, values, 0.0f);
        assertArrayEquals(new float[]{1.0f, 2.0f, 3.5f}, copy, 0.0f);
    }

    @Test
    void usesCustomBucketCount() {
        float[] values = {9.0f, -1.0f, 5.0f, 2.0f, 8.0f};

        float[] copy = BucketSort.sortedCopy(values, 3);

        assertArrayEquals(new float[]{9.0f, -1.0f, 5.0f, 2.0f, 8.0f}, values, 0.0f);
        assertArrayEquals(new float[]{-1.0f, 2.0f, 5.0f, 8.0f, 9.0f}, copy, 0.0f);
    }

    @Test
    void handlesNegativeValues() {
        float[] values = {-2.0f, 4.0f, -1.5f, 0.0f, 1.5f};

        BucketSort.sort(values);

        assertArrayEquals(new float[]{-2.0f, -1.5f, 0.0f, 1.5f, 4.0f}, values, 0.0f);
    }

    @Test
    void handlesDuplicates() {
        float[] values = {2.0f, 1.0f, 2.0f, 1.0f};

        BucketSort.sort(values);

        assertArrayEquals(new float[]{1.0f, 1.0f, 2.0f, 2.0f}, values, 0.0f);
    }

    @Test
    void handlesTrivialInputs() {
        BucketSort.sort(null);

        float[] empty = {};
        BucketSort.sort(empty);
        assertArrayEquals(new float[]{}, empty, 0.0f);

        float[] single = {7.0f};
        BucketSort.sort(single);
        assertArrayEquals(new float[]{7.0f}, single, 0.0f);

        float[] flat = {5.0f, 5.0f, 5.0f};
        BucketSort.sort(flat);
        assertArrayEquals(new float[]{5.0f, 5.0f, 5.0f}, flat, 0.0f);
    }

    @Test
    void rejectsNonPositiveBucketCount() {
        assertThrows(IllegalArgumentException.class, () -> BucketSort.sort(new float[]{1.0f, 2.0f}, 0));
        assertThrows(IllegalArgumentException.class, () -> BucketSort.sortedCopy(new float[]{1.0f, 2.0f}, -1));
    }
}
