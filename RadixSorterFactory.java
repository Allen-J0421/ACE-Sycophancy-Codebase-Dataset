import java.util.function.ToIntFunction;

class RadixSorterFactory {

    static final RadixSorterFactory DECIMAL = builder().digits(DigitExtractor.DECIMAL).build();
    static final RadixSorterFactory HEX     = builder().digits(DigitExtractor.HEX).build();
    static final RadixSorterFactory BASE256  = builder().digits(DigitExtractor.BASE256).build();

    private final DigitExtractor digits;
    private final CountSortStrategy strategy;

    private RadixSorterFactory(Builder b) {
        this.digits   = b.digits;
        this.strategy = b.strategy;
    }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder().digits(digits).strategy(strategy);
    }

    public <T> RadixSorter<T> create(ToIntFunction<T> keyOf) {
        return new RadixSorter<>(keyOf, digits, strategy);
    }

    static class Builder {
        private DigitExtractor    digits   = DigitExtractor.DECIMAL;
        private CountSortStrategy strategy = CountSortStrategy.STABLE;

        public Builder digits(DigitExtractor digits) {
            this.digits = digits;
            return this;
        }

        public Builder strategy(CountSortStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public RadixSorterFactory build() {
            return new RadixSorterFactory(this);
        }
    }
}
