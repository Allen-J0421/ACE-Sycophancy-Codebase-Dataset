import java.util.LinkedHashMap;
import java.util.Map;

final class SearchConfiguration {
    private static final int FULL_ARRAY_HIGH = -1;
    private static final int MAX_BOUNDS_CACHE_SIZE = 128;
    private static final SearchConfiguration FULL_ARRAY = new SearchConfiguration(0, FULL_ARRAY_HIGH);
    private static final Map<BoundsKey, SearchConfiguration> BOUNDS_CACHE =
            new LinkedHashMap<BoundsKey, SearchConfiguration>(
                    MAX_BOUNDS_CACHE_SIZE,
                    0.75f,
                    true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<BoundsKey, SearchConfiguration> eldest) {
                    return size() > MAX_BOUNDS_CACHE_SIZE;
                }
            };

    private final int low;
    private final int high;

    private SearchConfiguration(int low, int high) {
        this.low = low;
        this.high = high;
    }

    static SearchConfiguration fullArray() {
        return FULL_ARRAY;
    }

    static Builder builder() {
        return new Builder();
    }

    static SearchConfiguration withBounds(int low, int high) {
        return builder().withBounds(low, high).build();
    }

    private static SearchConfiguration boundedConfiguration(int low, int high) {
        validateBounds(low, high);

        BoundsKey key = new BoundsKey(low, high);
        synchronized (BOUNDS_CACHE) {
            SearchConfiguration configuration = BOUNDS_CACHE.get(key);
            if (configuration == null) {
                configuration = new SearchConfiguration(low, high);
                BOUNDS_CACHE.put(key, configuration);
            }

            return configuration;
        }
    }

    private static void validateBounds(int low, int high) {
        if (low < 0) {
            throw new IllegalArgumentException("low must be non-negative");
        }

        if (high < low) {
            throw new IllegalArgumentException("high must be greater than or equal to low");
        }
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

    static final class Builder {
        private boolean bounded;
        private boolean lowConfigured;
        private boolean highConfigured;
        private int low;
        private int high;

        private Builder() {
        }

        Builder fullArray() {
            bounded = false;
            lowConfigured = false;
            highConfigured = false;
            low = 0;
            high = FULL_ARRAY_HIGH;
            return this;
        }

        Builder withBounds(int low, int high) {
            bounded = true;
            lowConfigured = true;
            highConfigured = true;
            this.low = low;
            this.high = high;
            return this;
        }

        Builder withLowBound(int low) {
            bounded = true;
            lowConfigured = true;
            this.low = low;
            return this;
        }

        Builder withHighBound(int high) {
            bounded = true;
            highConfigured = true;
            this.high = high;
            return this;
        }

        SearchConfiguration build() {
            if (!bounded) {
                return SearchConfiguration.fullArray();
            }

            if (!lowConfigured || !highConfigured) {
                throw new IllegalStateException("both low and high bounds must be configured");
            }

            return SearchConfiguration.boundedConfiguration(low, high);
        }
    }

    private static final class BoundsKey {
        private final int low;
        private final int high;

        BoundsKey(int low, int high) {
            this.low = low;
            this.high = high;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof BoundsKey)) {
                return false;
            }

            BoundsKey otherKey = (BoundsKey) other;
            return low == otherKey.low && high == otherKey.high;
        }

        @Override
        public int hashCode() {
            return 31 * low + high;
        }
    }
}
