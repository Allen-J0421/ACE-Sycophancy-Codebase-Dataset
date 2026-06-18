import java.util.Comparator;

public enum SortProfile {
    SPEED {
        @Override
        public <T extends Comparable<T>> SortConfiguration<T> create() {
            return SortConfiguration.<T>builder()
                    .withComparator(Comparable::compareTo)
                    .withThreshold(50)
                    .withValidation(false)
                    .withStatistics(false)
                    .build();
        }

        @Override
        public String description() {
            return "Optimized for speed - minimal overhead";
        }
    },
    BALANCED {
        @Override
        public <T extends Comparable<T>> SortConfiguration<T> create() {
            return SortConfiguration.<T>builder()
                    .withComparator(Comparable::compareTo)
                    .withThreshold(10)
                    .withValidation(false)
                    .withStatistics(true)
                    .build();
        }

        @Override
        public String description() {
            return "Balanced between speed and features";
        }
    },
    SAFE {
        @Override
        public <T extends Comparable<T>> SortConfiguration<T> create() {
            return SortConfiguration.<T>builder()
                    .withComparator(Comparable::compareTo)
                    .withThreshold(10)
                    .withValidation(true)
                    .withStatistics(true)
                    .withLogger(new SortConfiguration.ConsoleLogger())
                    .build();
        }

        @Override
        public String description() {
            return "Prioritizes correctness with full validation";
        }
    },
    DEBUG {
        @Override
        public <T extends Comparable<T>> SortConfiguration<T> create() {
            return SortConfiguration.<T>builder()
                    .withComparator(Comparable::compareTo)
                    .withThreshold(5)
                    .withValidation(true)
                    .withStatistics(true)
                    .withLogger(new SortConfiguration.ConsoleLogger())
                    .build();
        }

        @Override
        public String description() {
            return "Maximum logging and validation for debugging";
        }
    };

    public abstract <T extends Comparable<T>> SortConfiguration<T> create();
    public abstract String description();
}
