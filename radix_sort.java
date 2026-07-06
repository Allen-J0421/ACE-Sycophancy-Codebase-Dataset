import java.util.Arrays;
import java.util.function.ToIntFunction;

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

class RadixSorter<T> {

    private final ToIntFunction<T> keyOf;
    private final DigitExtractor digits;

    public RadixSorter(ToIntFunction<T> keyOf) {
        this(keyOf, DigitExtractor.DECIMAL);
    }

    public RadixSorter(ToIntFunction<T> keyOf, DigitExtractor digits) {
        this.keyOf = keyOf;
        this.digits = digits;
    }

    public void sort(T[] arr) {
        int max = getMax(arr);
        for (int pass = 0; pass < digits.passCount(max); pass++)
            countSort(arr, pass);
    }

    private int getMax(T[] arr) {
        int max = keyOf.applyAsInt(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            int k = keyOf.applyAsInt(arr[i]);
            if (k > max) max = k;
        }
        return max;
    }

    @SuppressWarnings("unchecked")
    private void countSort(T[] arr, int pass) {
        // new T[n] is illegal due to type erasure; Object[] is safe because we only read T back out
        T[] output = (T[]) new Object[arr.length];
        int[] count = new int[digits.base()];

        for (T item : arr)
            count[digits.digit(keyOf.applyAsInt(item), pass)]++;

        for (int i = 1; i < digits.base(); i++)
            count[i] += count[i - 1];

        for (int i = arr.length - 1; i >= 0; i--) {
            int bucket = digits.digit(keyOf.applyAsInt(arr[i]), pass);
            output[count[bucket] - 1] = arr[i];
            count[bucket]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }
}

class Radix {

    public static void main(String[] args) {
        Integer[] numbers = { 170, 45, 75, 90, 802, 24, 2, 66 };
        new RadixSorter<>(Integer::intValue).sort(numbers);
        System.out.println("Decimal: " + Arrays.toString(numbers));

        Integer[] hex = { 170, 45, 75, 90, 802, 24, 2, 66 };
        new RadixSorter<>(Integer::intValue, DigitExtractor.HEX).sort(hex);
        System.out.println("Hex:      " + Arrays.toString(hex));

        Integer[] b256 = { 170, 45, 75, 90, 802, 24, 2, 66 };
        new RadixSorter<>(Integer::intValue, DigitExtractor.BASE256).sort(b256);
        System.out.println("Base-256: " + Arrays.toString(b256));

        String[] words = { "banana", "fig", "apple", "kiwi", "date" };
        new RadixSorter<>(String::length).sort(words);
        System.out.println("By length: " + Arrays.toString(words));
    }
}
