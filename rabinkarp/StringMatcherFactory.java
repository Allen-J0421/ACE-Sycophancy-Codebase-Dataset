package rabinkarp;

public final class StringMatcherFactory {
    private StringMatcherFactory() {}

    public static StringMatcher rabinKarp(int radix, int modulus) {
        return new RabinKarpMatcher(radix, modulus);
    }

    public static StringMatcher rollingHash(HashCalculator calculator) {
        return new RollingHashMatcher(calculator);
    }
}
