package sorting.core;

public enum OptimizationProfile {
    SPEED("Speed optimized") {
        @Override
        public <T extends Comparable<T>> SortConfig<T> apply(SortConfig.Builder<T> builder) throws SortConfigurationException {
            return builder
                    .comparator((a, b) -> ((Comparable) a).compareTo(b))
                    .insertionThreshold(50)
                    .trackMetrics(false)
                    .validateInput(false)
                    .threadPoolSize(Runtime.getRuntime().availableProcessors())
                    .build();
        }
    },

    BALANCED("Balanced performance") {
        @Override
        public <T extends Comparable<T>> SortConfig<T> apply(SortConfig.Builder<T> builder) throws SortConfigurationException {
            return builder
                    .comparator((a, b) -> ((Comparable) a).compareTo(b))
                    .insertionThreshold(20)
                    .trackMetrics(true)
                    .validateInput(true)
                    .threadPoolSize(Math.max(1, Runtime.getRuntime().availableProcessors() / 2))
                    .build();
        }
    },

    SAFETY("Safety optimized") {
        @Override
        public <T extends Comparable<T>> SortConfig<T> apply(SortConfig.Builder<T> builder) throws SortConfigurationException {
            return builder
                    .comparator((a, b) -> ((Comparable) a).compareTo(b))
                    .insertionThreshold(10)
                    .trackMetrics(true)
                    .validateInput(true)
                    .threadPoolSize(1)
                    .build();
        }
    },

    MEMORY("Memory optimized") {
        @Override
        public <T extends Comparable<T>> SortConfig<T> apply(SortConfig.Builder<T> builder) throws SortConfigurationException {
            return builder
                    .comparator((a, b) -> ((Comparable) a).compareTo(b))
                    .insertionThreshold(5)
                    .trackMetrics(false)
                    .validateInput(false)
                    .threadPoolSize(1)
                    .build();
        }
    };

    private final String description;

    OptimizationProfile(String description) {
        this.description = description;
    }

    public abstract <T extends Comparable<T>> SortConfig<T> apply(SortConfig.Builder<T> builder) throws SortConfigurationException;

    public String getDescription() {
        return description;
    }

    public static <T extends Comparable<T>> SortConfig<T> recommended() throws SortConfigurationException {
        return BALANCED.apply(SortConfig.<T>builder());
    }
}
