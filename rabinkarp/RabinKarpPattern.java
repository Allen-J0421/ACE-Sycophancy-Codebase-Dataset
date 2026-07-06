package rabinkarp;

public final class RabinKarpPattern {
    private final CharSequence pattern;
    private final int length;
    private final int hash;
    private final int highOrderFactor;
    private final RabinKarpMatcher matcher;

    private RabinKarpPattern(CharSequence pattern, int length, int hash, int highOrderFactor, RabinKarpMatcher matcher) {
        this.pattern = pattern;
        this.length = length;
        this.hash = hash;
        this.highOrderFactor = highOrderFactor;
        this.matcher = matcher;
    }

    public static RabinKarpPattern compile(CharSequence pattern, int radix, int modulus) {
        PolynomialHashCalculator calculator = new PolynomialHashCalculator(radix, modulus);
        RabinKarpMatcher m = new RabinKarpMatcher(radix, modulus);
        CompiledPattern cp = CompiledPattern.compile(pattern, calculator);
        return new RabinKarpPattern(cp.pattern(), cp.length(), cp.hash(), cp.highOrderFactor(), m);
    }

    public static RabinKarpPattern compile(CharSequence pattern) {
        return compile(pattern, RabinKarpMatcher.DEFAULT_RADIX, RabinKarpMatcher.DEFAULT_MODULUS);
    }

    public MatchResult searchIn(CharSequence text) {
        return matcher.search(this, text);
    }

    CharSequence pattern() { return pattern; }
    int length() { return length; }
    int hash() { return hash; }
    int highOrderFactor() { return highOrderFactor; }
}
