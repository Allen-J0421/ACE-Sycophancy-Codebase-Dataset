final class SearchConfiguration {
    private static final int FULL_ARRAY_HIGH = -1;

    private final int low;
    private final int high;

    private SearchConfiguration(int low, int high) {
        this.low = low;
        this.high = high;
    }

    static SearchConfiguration fullArray() {
        return new SearchConfiguration(0, FULL_ARRAY_HIGH);
    }

    static SearchConfiguration withBounds(int low, int high) {
        if (low < 0) {
            throw new IllegalArgumentException("low must be non-negative");
        }

        if (high < low) {
            throw new IllegalArgumentException("high must be greater than or equal to low");
        }

        return new SearchConfiguration(low, high);
    }

    int low() {
        return low;
    }

    int high() {
        return high;
    }

    int highForArrayLength(int arrayLength) {
        if (arrayLength < 0) {
            throw new IllegalArgumentException("arrayLength must be non-negative");
        }

        if (high == FULL_ARRAY_HIGH) {
            return arrayLength - 1;
        }

        if (high >= arrayLength) {
            throw new IllegalArgumentException("high must be less than array length");
        }

        return high;
    }
}
