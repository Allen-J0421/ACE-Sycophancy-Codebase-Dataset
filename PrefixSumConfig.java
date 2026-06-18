/**
 * Configuration object for PrefixSum behavior.
 */
public class PrefixSumConfig {
    private final boolean cacheEnabled;
    private final boolean clearCacheOnCompute;
    private final boolean trackMetrics;

    private PrefixSumConfig(boolean cacheEnabled, boolean clearCacheOnCompute, boolean trackMetrics) {
        this.cacheEnabled = cacheEnabled;
        this.clearCacheOnCompute = clearCacheOnCompute;
        this.trackMetrics = trackMetrics;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public boolean shouldClearCacheOnCompute() {
        return clearCacheOnCompute;
    }

    public boolean isTrackMetricsEnabled() {
        return trackMetrics;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for fluent configuration.
     */
    public static class Builder {
        private boolean cacheEnabled = false;
        private boolean clearCacheOnCompute = false;
        private boolean trackMetrics = false;

        public Builder withCache() {
            this.cacheEnabled = true;
            return this;
        }

        public Builder withCacheClear() {
            this.clearCacheOnCompute = true;
            return this;
        }

        public Builder withMetrics() {
            this.trackMetrics = true;
            return this;
        }

        public PrefixSumConfig build() {
            return new PrefixSumConfig(cacheEnabled, clearCacheOnCompute, trackMetrics);
        }
    }

    public static PrefixSumConfig defaults() {
        return new PrefixSumConfig(false, false, false);
    }
}
