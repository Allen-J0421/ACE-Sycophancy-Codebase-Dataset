import java.util.Comparator;

public class SortConfiguration<T> {
    private final Comparator<T> comparator;
    private final int insertionSortThreshold;
    private final boolean validateInput;
    private final boolean trackStatistics;
    private final SortLogger logger;

    public SortConfiguration(Builder<T> builder) {
        this.comparator = builder.comparator;
        this.insertionSortThreshold = builder.insertionSortThreshold;
        this.validateInput = builder.validateInput;
        this.trackStatistics = builder.trackStatistics;
        this.logger = builder.logger;
    }

    public static <T extends Comparable<T>> Builder<T> builder() {
        return new Builder<>();
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public int getInsertionSortThreshold() {
        return insertionSortThreshold;
    }

    public boolean isValidateInput() {
        return validateInput;
    }

    public boolean isTrackStatistics() {
        return trackStatistics;
    }

    public SortLogger getLogger() {
        return logger;
    }

    public static class Builder<T> {
        private Comparator<T> comparator;
        private int insertionSortThreshold = 10;
        private boolean validateInput = true;
        private boolean trackStatistics = true;
        private SortLogger logger = new NoOpLogger();

        public Builder<T> withComparator(Comparator<T> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Builder<T> withThreshold(int threshold) {
            this.insertionSortThreshold = threshold;
            return this;
        }

        public Builder<T> withValidation(boolean validate) {
            this.validateInput = validate;
            return this;
        }

        public Builder<T> withStatistics(boolean track) {
            this.trackStatistics = track;
            return this;
        }

        public Builder<T> withLogger(SortLogger logger) {
            this.logger = logger;
            return this;
        }

        public SortConfiguration<T> build() {
            if (comparator == null) {
                throw new IllegalStateException("Comparator must be set");
            }
            return new SortConfiguration<>(this);
        }
    }

    interface SortLogger {
        void log(String message);
    }

    static class NoOpLogger implements SortLogger {
        @Override
        public void log(String message) {
        }
    }

    static class ConsoleLogger implements SortLogger {
        @Override
        public void log(String message) {
            System.out.println("[SORT] " + message);
        }
    }
}
