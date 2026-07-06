import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

interface Sortable<T> {
    int size();
    T get(int index);
    void set(int index, T value);

    static <T> Sortable<T> of(T[] arr)    { return new ArraySortable<>(arr); }
    static <T> Sortable<T> of(List<T> list) { return new ListSortable<>(list); }
}

class ArraySortable<T> implements Sortable<T> {
    private final T[] arr;
    ArraySortable(T[] arr) { this.arr = arr; }
    public int size()              { return arr.length; }
    public T get(int i)            { return arr[i]; }
    public void set(int i, T v)    { arr[i] = v; }
}

class ListSortable<T> implements Sortable<T> {
    private final List<T> list;
    ListSortable(List<T> list) { this.list = list; }
    public int size()              { return list.size(); }
    public T get(int i)            { return list.get(i); }
    public void set(int i, T v)    { list.set(i, v); }
}

interface DigitExtractor {
    int base();
    int digit(int key, int pass);
    int passCount(int maxKey);

    static DigitExtractor ofBase(int base) {
        if (base >= 2 && (base & (base - 1)) == 0)
            return new BitShiftExtractor(base);
        return new BaseExtractor(base);
    }

    DigitExtractor DECIMAL  = ofBase(10);
    DigitExtractor HEX      = ofBase(16);
    DigitExtractor BASE256  = ofBase(256);
}

class BitShiftExtractor implements DigitExtractor {

    private final int base;
    private final int shift; // bits per digit: log2(base)
    private final int mask;  // base - 1: isolates one digit via bitwise AND

    BitShiftExtractor(int base) {
        this.base  = base;
        this.shift = Integer.numberOfTrailingZeros(base);
        this.mask  = base - 1;
    }

    public int base() { return base; }

    public int digit(int key, int pass) {
        return (key >>> (pass * shift)) & mask;
    }

    public int passCount(int maxKey) {
        if (maxKey == 0) return 1;
        return (Integer.SIZE - Integer.numberOfLeadingZeros(maxKey) + shift - 1) / shift;
    }
}

class BaseExtractor implements DigitExtractor {

    private final int base;

    BaseExtractor(int base) {
        this.base = base;
    }

    public int base() { return base; }

    public int digit(int key, int pass) {
        int divisor = 1;
        for (int p = 0; p < pass; p++) divisor *= base;
        return (key / divisor) % base;
    }

    public int passCount(int maxKey) {
        if (maxKey == 0) return 1;
        int passes = 0;
        for (int n = maxKey; n > 0; n /= base) passes++;
        return passes;
    }
}

interface CountSortStrategy {
    <T> void apply(Sortable<T> s, Pass<T> pass);

    CountSortStrategy STABLE      = new StableCountSort();
    CountSortStrategy BUCKET_LIST = new BucketListCountSort();
}

class StableCountSort implements CountSortStrategy {

    @SuppressWarnings("unchecked")
    public <T> void apply(Sortable<T> s, Pass<T> pass) {
        int n = s.size();
        // plain Object[] avoids the generic-array restriction; cast back to T on write-through is safe
        Object[] output = new Object[n];
        int[] count = new int[pass.base()];

        for (int i = 0; i < n; i++)
            count[pass.bucket(s.get(i))]++;

        for (int i = 1; i < pass.base(); i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int bucket = pass.bucket(s.get(i));
            output[count[bucket] - 1] = s.get(i);
            count[bucket]--;
        }

        for (int i = 0; i < n; i++)
            s.set(i, (T) output[i]);
    }
}

class BucketListCountSort implements CountSortStrategy {

    @SuppressWarnings("unchecked")
    public <T> void apply(Sortable<T> s, Pass<T> pass) {
        List<T>[] buckets = new List[pass.base()];
        for (int i = 0; i < pass.base(); i++) buckets[i] = new ArrayList<>();

        for (int i = 0; i < s.size(); i++)
            buckets[pass.bucket(s.get(i))].add(s.get(i));

        int pos = 0;
        for (List<T> bucket : buckets)
            for (T item : bucket) s.set(pos++, item);
    }
}

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

class Pass<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;
    private final int index;

    Pass(ToIntFunction<T> keyOf, DigitExtractor digits, int index) {
        this.keyOf  = keyOf;
        this.digits = digits;
        this.index  = index;
    }

    int base()         { return digits.base(); }
    int bucket(T item) { return digits.digit(keyOf.applyAsInt(item), index); }
}

class RadixSorter<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;
    private final CountSortStrategy strategy;

    RadixSorter(ToIntFunction<T> keyOf, DigitExtractor digits, CountSortStrategy strategy) {
        this.keyOf = keyOf;
        this.digits = digits;
        this.strategy = strategy;
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

class Radix {

    public static void main(String[] args) {
        // named constant — zero configuration at the call site
        Integer[] arr1 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.DECIMAL.create(Integer::intValue).sort(Sortable.of(arr1));
        System.out.println("Named constant:  " + Arrays.toString(arr1));

        // full builder — compose digits and strategy from scratch
        Integer[] arr2 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.builder()
            .digits(DigitExtractor.HEX)
            .strategy(CountSortStrategy.BUCKET_LIST)
            .build()
            .create(Integer::intValue)
            .sort(Sortable.of(arr2));
        System.out.println("Full builder:    " + Arrays.toString(arr2));

        // toBuilder — copy an existing config and override one field
        Integer[] arr3 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        RadixSorterFactory.DECIMAL.toBuilder()
            .strategy(CountSortStrategy.BUCKET_LIST)
            .build()
            .create(Integer::intValue)
            .sort(Sortable.of(arr3));
        System.out.println("toBuilder:       " + Arrays.toString(arr3));

        List<String> words = new ArrayList<>(Arrays.asList("banana", "fig", "apple", "kiwi", "date"));
        RadixSorterFactory.HEX.create(String::length).sort(Sortable.of(words));
        System.out.println("By length:       " + words);
    }
}
