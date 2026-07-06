import java.util.function.ToIntFunction;

class RadixSorter<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;
    private final CountSortStrategy strategy;

    RadixSorter(ToIntFunction<T> keyOf, DigitExtractor digits, CountSortStrategy strategy) {
        this.keyOf     = keyOf;
        this.digits    = digits;
        this.strategy  = strategy;
    }

    public void sort(Sortable<T> s) {
        int max = getMax(s);
        for (int p = 0; p < digits.passCount(max); p++)
            strategy.apply(s, new Pass<>(keyOf, digits, p));
    }

    private int getMax(Sortable<T> s) {
        int max = keyOf.applyAsInt(s.get(0));
        for (int i = 1; i < s.size(); i++) {
            int k = keyOf.applyAsInt(s.get(i));
            if (k > max) max = k;
        }
        return max;
    }
}
