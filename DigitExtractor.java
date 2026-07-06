interface DigitExtractor {
    int base();
    int digit(int key, int pass);
    int passCount(int maxKey);

    static DigitExtractor ofBase(int base) {
        if (base >= 2 && (base & (base - 1)) == 0)
            return new BitShiftExtractor(base);
        return new BaseExtractor(base);
    }

    DigitExtractor DECIMAL = ofBase(10);
    DigitExtractor HEX     = ofBase(16);
    DigitExtractor BASE256 = ofBase(256);
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
