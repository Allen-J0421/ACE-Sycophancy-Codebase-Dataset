package rabinkarp;

import java.util.Objects;

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
        Objects.requireNonNull(pattern, "pattern");

        int length = pattern.length();
        int hash = 0;
        int highOrderFactor = 1;

        for (int i = 0; i < length - 1; i++) {
            highOrderFactor = (highOrderFactor * radix) % modulus;
        }
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + pattern.charAt(i)) % modulus;
        }

        return new CompiledPattern(pattern, length, hash, highOrderFactor);
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
