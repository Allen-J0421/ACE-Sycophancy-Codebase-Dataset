package rabinkarp;

public final class StringMatcherFactory {
    private StringMatcherFactory() {}

    public static StringMatcher rabinKarp() {
        return new RabinKarpMatcher(RabinKarpMatcher.DEFAULT_RADIX, RabinKarpMatcher.DEFAULT_MODULUS);
    }

    public static StringMatcher rabinKarp(int radix, int modulus) {
        return new RabinKarpMatcher(radix, modulus);
    }

    public static StringMatcher naive() {
        return new NaiveMatcher();
    }
}
