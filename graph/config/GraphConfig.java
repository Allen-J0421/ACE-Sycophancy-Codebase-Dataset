package graph.config;

public class GraphConfig {
    private final boolean allowSelfLoops;
    private final boolean cacheAnalysis;
    private final long cacheTtlMs;
    private final boolean enableLogging;
    private final boolean enableStats;

    private GraphConfig(Builder builder) {
        this.allowSelfLoops = builder.allowSelfLoops;
        this.cacheAnalysis = builder.cacheAnalysis;
        this.cacheTtlMs = builder.cacheTtlMs;
        this.enableLogging = builder.enableLogging;
        this.enableStats = builder.enableStats;
    }

    public boolean isAllowSelfLoops() {
        return allowSelfLoops;
    }

    public boolean isCacheAnalysis() {
        return cacheAnalysis;
    }

    public long getCacheTtlMs() {
        return cacheTtlMs;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public boolean isEnableStats() {
        return enableStats;
    }

    public static class Builder {
        private boolean allowSelfLoops = false;
        private boolean cacheAnalysis = true;
        private long cacheTtlMs = 60000;
        private boolean enableLogging = false;
        private boolean enableStats = true;

        public Builder allowSelfLoops(boolean allow) {
            this.allowSelfLoops = allow;
            return this;
        }

        public Builder cacheAnalysis(boolean cache) {
            this.cacheAnalysis = cache;
            return this;
        }

        public Builder cacheTtlMs(long ttl) {
            this.cacheTtlMs = ttl;
            return this;
        }

        public Builder enableLogging(boolean enable) {
            this.enableLogging = enable;
            return this;
        }

        public Builder enableStats(boolean enable) {
            this.enableStats = enable;
            return this;
        }

        public GraphConfig build() {
            return new GraphConfig(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GraphConfig defaultConfig() {
        return new Builder().build();
    }
}
