package sorting.core;

import java.util.Comparator;
import java.util.Objects;

public record SortConfig<T extends Comparable<T>>(
        Comparator<T> comparator,
        int insertionThreshold,
        boolean trackMetrics,
        boolean validateInput,
        int threadPoolSize) {

    public SortConfig {
        Objects.requireNonNull(comparator, "comparator cannot be null");
        if (insertionThreshold < 1) {
            throw new IllegalArgumentException("insertionThreshold must be >= 1");
        }
        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("threadPoolSize must be >= 1");
        }
    }

    public static <T extends Comparable<T>> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends Comparable<T>> {
        private Comparator<T> comparator;
        private int insertionThreshold = 10;
        private boolean trackMetrics = true;
        private boolean validateInput = true;
        private int threadPoolSize = 1;

        public Builder<T> comparator(Comparator<T> comp) {
            this.comparator = comp;
            return this;
        }

        public Builder<T> insertionThreshold(int threshold) {
            this.insertionThreshold = threshold;
            return this;
        }

        public Builder<T> trackMetrics(boolean track) {
            this.trackMetrics = track;
            return this;
        }

        public Builder<T> validateInput(boolean validate) {
            this.validateInput = validate;
            return this;
        }

        public Builder<T> threadPoolSize(int size) {
            this.threadPoolSize = size;
            return this;
        }

        public Builder<T> parallel() {
            this.threadPoolSize = Runtime.getRuntime().availableProcessors();
            return this;
        }

        public SortConfig<T> build() throws SortConfigurationException {
            if (comparator == null) {
                throw new SortConfigurationException("Comparator must be set");
            }
            return new SortConfig<>(comparator, insertionThreshold, trackMetrics, validateInput, threadPoolSize);
        }
    }

    public boolean isParallel() {
        return threadPoolSize > 1;
    }
}
