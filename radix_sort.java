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

class RadixSorterFactory {

    static final RadixSorterFactory DECIMAL = new RadixSorterFactory(DigitExtractor.DECIMAL);
    static final RadixSorterFactory HEX     = new RadixSorterFactory(DigitExtractor.HEX);
    static final RadixSorterFactory BASE256  = new RadixSorterFactory(DigitExtractor.BASE256);

    private final DigitExtractor digits;

    public RadixSorterFactory(DigitExtractor digits) {
        this.digits = digits;
    }

    public <T> RadixSorter<T> create(ToIntFunction<T> keyOf) {
        return new RadixSorter<>(keyOf, digits);
    }
}

class RadixSorter<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;

    RadixSorter(ToIntFunction<T> keyOf, DigitExtractor digits) {
        this.keyOf = keyOf;
        this.digits = digits;
    }

    public void sort(Sortable<T> s) {
        int max = getMax(s);
        for (int pass = 0; pass < digits.passCount(max); pass++)
            countSort(s, pass);
    }

    private int getMax(Sortable<T> s) {
        int max = keyOf.applyAsInt(s.get(0));
        for (int i = 1; i < s.size(); i++) {
            int k = keyOf.applyAsInt(s.get(i));
            if (k > max) max = k;
        }
        return max;
    }

    @SuppressWarnings("unchecked")
    private void countSort(Sortable<T> s, int pass) {
        int n = s.size();
        // plain Object[] avoids the generic-array restriction; cast back to T on write-through is safe
        Object[] output = new Object[n];
        int[] count = new int[digits.base()];

        for (int i = 0; i < n; i++)
            count[digits.digit(keyOf.applyAsInt(s.get(i)), pass)]++;

        for (int i = 1; i < digits.base(); i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int bucket = digits.digit(keyOf.applyAsInt(s.get(i)), pass);
            output[count[bucket] - 1] = s.get(i);
            count[bucket]--;
        }

        for (int i = 0; i < n; i++)
            s.set(i, (T) output[i]);
    }
}

class Radix {

    public static void main(String[] args) {
        RadixSorterFactory decimal = RadixSorterFactory.DECIMAL;
        RadixSorterFactory hex     = RadixSorterFactory.HEX;

        Integer[] arr = { 170, 45, 75, 90, 802, 24, 2, 66 };
        decimal.create(Integer::intValue).sort(Sortable.of(arr));
        System.out.println("Array/decimal: " + Arrays.toString(arr));

        List<Integer> list = new ArrayList<>(Arrays.asList(170, 45, 75, 90, 802, 24, 2, 66));
        hex.create(Integer::intValue).sort(Sortable.of(list));
        System.out.println("List/hex:      " + list);

        List<String> words = new ArrayList<>(Arrays.asList("banana", "fig", "apple", "kiwi", "date"));
        decimal.create(String::length).sort(Sortable.of(words));
        System.out.println("List/length:   " + words);
    }
}
