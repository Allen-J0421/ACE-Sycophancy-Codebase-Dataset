package rabinkarp;

final class CompiledPattern {
    private final CharSequence pattern;
    private final int length;
    private final int hash;
    private final int highOrderFactor;

    private CompiledPattern(CharSequence pattern, int length, int hash, int highOrderFactor) {
        this.pattern = pattern;
        this.length = length;
        this.hash = hash;
        this.highOrderFactor = highOrderFactor;
    }

    static CompiledPattern compile(CharSequence pattern, int radix, int modulus) {
        int m = pattern.length();
        int h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * radix) % modulus;
        }
        int hash = 0;
        for (int i = 0; i < m; i++) {
            hash = (radix * hash + pattern.charAt(i)) % modulus;
        }
        return new CompiledPattern(pattern, m, hash, h);
    }

    CharSequence pattern() { return pattern; }
    int length() { return length; }
    int hash() { return hash; }
    int highOrderFactor() { return highOrderFactor; }
}
