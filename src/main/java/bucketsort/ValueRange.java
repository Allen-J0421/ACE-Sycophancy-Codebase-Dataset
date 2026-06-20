package bucketsort;

final class ValueRange {

    private final float min;
    private final float span;

    private ValueRange(float min, float max) {
        this.min = min;
        this.span = max - min;
    }

    static ValueRange from(float[] values) {
        float min = values[0];
        float max = values[0];

        for (float value : values) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        return new ValueRange(min, max);
    }

    boolean isFlat() {
        return span == 0.0f;
    }

    int bucketIndexFor(float value, int bucketCount) {
        int bucketIndex = (int) (((value - min) / span) * (bucketCount - 1));
        if (bucketIndex < 0) {
            return 0;
        }
        if (bucketIndex >= bucketCount) {
            return bucketCount - 1;
        }
        return bucketIndex;
    }
}
