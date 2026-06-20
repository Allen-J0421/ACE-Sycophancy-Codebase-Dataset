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

        DistributionPlan distributionPlan = DistributionPlan.from(values);
        float[] bucketedValues = distribute(values, distributionPlan);
        sortBuckets(bucketedValues, distributionPlan);
        System.arraycopy(bucketedValues, 0, values, 0, values.length);
    }

    private static float[] distribute(float[] values, DistributionPlan distributionPlan) {
        float[] bucketedValues = new float[values.length];
        int[] insertionIndexes = distributionPlan.insertionIndexes();
        for (int i = 0; i < values.length; i++) {
            int bucketIndex = distributionPlan.bucketIndexForValue(i);
            bucketedValues[insertionIndexes[bucketIndex]++] = values[i];
        }
        return bucketedValues;
    }

    private static void sortBuckets(float[] bucketedValues, DistributionPlan distributionPlan) {
        for (int i = 0; i < distributionPlan.bucketCount(); i++) {
            insertionSort(
                bucketedValues,
                distributionPlan.bucketStart(i),
                distributionPlan.bucketEnd(i)
            );
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

    private static final class DistributionPlan {

        private final int[] bucketBoundaries;
        private final int[] bucketIndexesByValue;

        private DistributionPlan(int[] bucketBoundaries, int[] bucketIndexesByValue) {
            this.bucketBoundaries = bucketBoundaries;
            this.bucketIndexesByValue = bucketIndexesByValue;
        }

        private static DistributionPlan from(float[] values) {
            int[] bucketSizes = new int[values.length];
            int[] bucketIndexesByValue = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                float value = values[i];
                validateValue(value);
                int bucketIndex = bucketIndexFor(value, bucketSizes.length);
                bucketIndexesByValue[i] = bucketIndex;
                bucketSizes[bucketIndex]++;
            }

            int[] bucketBoundaries = new int[bucketSizes.length + 1];
            int nextBucketStart = 0;
            for (int i = 0; i < bucketSizes.length; i++) {
                bucketBoundaries[i] = nextBucketStart;
                nextBucketStart += bucketSizes[i];
            }
            bucketBoundaries[bucketSizes.length] = nextBucketStart;

            return new DistributionPlan(bucketBoundaries, bucketIndexesByValue);
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

        private int bucketIndexForValue(int valueIndex) {
            return bucketIndexesByValue[valueIndex];
        }

        private int[] insertionIndexes() {
            return Arrays.copyOf(bucketBoundaries, bucketCount());
        }
    }
}
