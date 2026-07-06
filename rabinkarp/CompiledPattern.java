package rabinkarp;

import stringsearch.HashCalculator;

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

    static CompiledPattern compile(CharSequence pattern, HashCalculator calculator) {
        int m = pattern.length();
        int hash = calculator.hash(pattern, m);
        int h = calculator.highOrderFactor(m);
        return new CompiledPattern(pattern, m, hash, h);
    }

    CharSequence pattern() { return pattern; }
    int length() { return length; }
    int hash() { return hash; }
    int highOrderFactor() { return highOrderFactor; }
}
