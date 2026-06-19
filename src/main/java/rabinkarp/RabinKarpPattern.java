package rabinkarp;

import java.util.Objects;

public final class RabinKarpPattern {

    private final CharSequence pattern;
    private final int length;
    private final int hash;
    private final int highOrderFactor;
    private final int radix;
    private final int modulus;

    private RabinKarpPattern(
            CharSequence pattern, int length, int hash, int highOrderFactor, int radix, int modulus) {
        this.pattern = pattern;
        this.length = length;
        this.hash = hash;
        this.highOrderFactor = highOrderFactor;
        this.radix = radix;
        this.modulus = modulus;
    }

    public static RabinKarpPattern compile(CharSequence pattern, int radix, int modulus) {
        Objects.requireNonNull(pattern, "pattern");
        if (radix <= 0) {
            throw new IllegalArgumentException("radix must be positive");
        }
        if (modulus <= 1) {
            throw new IllegalArgumentException("modulus must be greater than 1");
        }

        int length = pattern.length();
        int hash = 0;
        int highOrderFactor = 1;

        for (int i = 0; i < length - 1; i++) {
            highOrderFactor = (highOrderFactor * radix) % modulus;
        }
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + pattern.charAt(i)) % modulus;
        }

        return new RabinKarpPattern(pattern, length, hash, highOrderFactor, radix, modulus);
    }

    public static RabinKarpPattern compile(CharSequence pattern) {
        return compile(
                pattern,
                RabinKarpMatcher.DEFAULT_RADIX,
                RabinKarpMatcher.DEFAULT_MODULUS);
    }

    public java.util.List<Integer> searchIn(CharSequence text) {
        return new RabinKarpMatcher(radix, modulus).search(this, text);
    }

    CharSequence pattern() {
        return pattern;
    }

    int length() {
        return length;
    }

    int hash() {
        return hash;
    }

    int highOrderFactor() {
        return highOrderFactor;
    }
}
