import java.util.Arrays;
import java.util.Objects;

public final class BucketSort {

    private static final float MIN_SUPPORTED_VALUE = 0.0f;
    private static final float MAX_SUPPORTED_VALUE = 1.0f;

    private BucketSort() {
    }

    public static void sort(float[] values) {
        Objects.requireNonNull(values, "values");
        sortInPlace(values);
    }

    public static float[] sortedCopy(float[] values) {
        Objects.requireNonNull(values, "values");

        float[] copy = Arrays.copyOf(values, values.length);
        sortInPlace(copy);
        return copy;
    }

    private static void sortInPlace(float[] values) {
        if (values.length == 0) {
            return;
        }

        BucketLayout bucketLayout = BucketLayout.from(values);
        float[] bucketedValues = distribute(values, bucketLayout);
        sortBuckets(bucketedValues, bucketLayout);
        System.arraycopy(bucketedValues, 0, values, 0, values.length);
    }

    private static float[] distribute(float[] values, BucketLayout bucketLayout) {
        float[] bucketedValues = new float[values.length];
        int[] insertionIndexes = bucketLayout.insertionIndexes();
        for (float value : values) {
            int bucketIndex = bucketIndexFor(value, bucketLayout.bucketCount());
            bucketedValues[insertionIndexes[bucketIndex]++] = value;
        }
        return bucketedValues;
    }

    private static void sortBuckets(float[] bucketedValues, BucketLayout bucketLayout) {
        for (int i = 0; i < bucketLayout.bucketCount(); i++) {
            insertionSort(bucketedValues, bucketLayout.bucketStart(i), bucketLayout.bucketEnd(i));
        }
    }

    private static void insertionSort(float[] values, int startInclusive, int endExclusive) {
        for (int i = startInclusive + 1; i < endExclusive; i++) {
            float currentValue = values[i];
            int j = i - 1;
            while (j >= startInclusive && values[j] > currentValue) {
                values[j + 1] = values[j];
                j--;
            }
            values[j + 1] = currentValue;
        }
    }

    private static int bucketIndexFor(float value, int bucketCount) {
        return (int) (bucketCount * value);
    }

    private static void validateValue(float value) {
        if (!isSupportedValue(value)) {
            throw new IllegalArgumentException(
                "Bucket sort expects values in the range ["
                    + MIN_SUPPORTED_VALUE
                    + ", "
                    + MAX_SUPPORTED_VALUE
                    + "): "
                    + value
            );
        }
    }

    private static boolean isSupportedValue(float value) {
        return Float.isFinite(value)
            && value >= MIN_SUPPORTED_VALUE
            && value < MAX_SUPPORTED_VALUE;
    }

    private static final class BucketLayout {

        private final int[] bucketBoundaries;

        private BucketLayout(int[] bucketBoundaries) {
            this.bucketBoundaries = bucketBoundaries;
        }

        private static BucketLayout from(float[] values) {
            int[] bucketSizes = new int[values.length];

            for (float value : values) {
                validateValue(value);
                bucketSizes[bucketIndexFor(value, bucketSizes.length)]++;
            }

            int[] bucketBoundaries = new int[bucketSizes.length + 1];
            int nextBucketStart = 0;
            for (int i = 0; i < bucketSizes.length; i++) {
                bucketBoundaries[i] = nextBucketStart;
                nextBucketStart += bucketSizes[i];
            }
            bucketBoundaries[bucketSizes.length] = nextBucketStart;

            return new BucketLayout(bucketBoundaries);
        }

        private int bucketCount() {
            return bucketBoundaries.length - 1;
        }

        private int bucketStart(int bucketIndex) {
            return bucketBoundaries[bucketIndex];
        }

        private int bucketEnd(int bucketIndex) {
            return bucketBoundaries[bucketIndex + 1];
        }

        private int[] insertionIndexes() {
            return Arrays.copyOf(bucketBoundaries, bucketCount());
        }
    }
}
