/**
 * Configuration for binary search operations.
 * Encapsulates settings that control search behavior.
 */
class SearchConfig {
    private final boolean trackStats;

    private SearchConfig(Builder builder) {
        this.trackStats = builder.trackStats;
    }

    boolean isTrackStats() {
        return trackStats;
    }

    static Builder builder() {
        return new Builder();
    }

    static SearchConfig defaults() {
        return new Builder().build();
    }

    static class Builder {
        private boolean trackStats = false;

        public Builder withStats() {
            this.trackStats = true;
            return this;
        }

        public SearchConfig build() {
            return new SearchConfig(this);
        }
    }
}
