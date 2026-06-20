import java.util.ArrayList;
import java.util.List;

/**
 * Bucket sort for {@code float} arrays.
 *
 * <p>Unlike the textbook variant that assumes inputs in {@code [0, 1)}, this
 * implementation scales buckets to the actual {@code [min, max]} range of the
 * data, so it sorts arrays of arbitrary float values correctly.
 */
public final class BucketSort {

    private BucketSort() {
        // Utility class; not instantiable.
    }

    /**
     * Sorts {@code arr} in place in ascending order.
     *
     * @param arr the array to sort; {@code null} and arrays of length &lt; 2 are
     *            left unchanged
     */
    public static void sort(float[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        int n = arr.length;
        float min = arr[0];
        float max = arr[0];
        for (float v : arr) {
            if (v < min) {
                min = v;
            } else if (v > max) {
                max = v;
            }
        }

        // All values equal: already sorted.
        if (min == max) {
            return;
        }

        List<List<Float>> buckets = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            buckets.add(new ArrayList<>());
        }

        // Map each value to a bucket in [0, n - 1] based on its position in the range.
        float range = max - min;
        for (float v : arr) {
            int bi = (int) ((n - 1) * (v - min) / range);
            buckets.get(bi).add(v);
        }

        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }

        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float v : bucket) {
                arr[index++] = v;
            }
        }
    }

    /** Sorts a single bucket in place using insertion sort. */
    private static void insertionSort(List<Float> bucket) {
        for (int i = 1; i < bucket.size(); i++) {
            float key = bucket.get(i);
            int j = i - 1;
            while (j >= 0 && bucket.get(j) > key) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }
            bucket.set(j + 1, key);
        }
    }
}
