package rabinkarp;

public final class PolynomialHashCalculator implements HashCalculator {
    private final int radix;
    private final int modulus;

    public PolynomialHashCalculator(int radix, int modulus) {
        this.radix = radix;
        this.modulus = modulus;
    }

    @Override
    public int hash(CharSequence seq, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + seq.charAt(i)) % modulus;
        }
        return hash;
    }

    @Override
    public int highOrderFactor(int patternLength) {
        int h = 1;
        for (int i = 0; i < patternLength - 1; i++) {
            h = (h * radix) % modulus;
        }
        return h;
    }

    @Override
    public int roll(int currentHash, char leaving, char entering, int highOrderFactor) {
        int hash = (radix * (currentHash - leaving * highOrderFactor) + entering) % modulus;
        if (hash < 0) hash += modulus;
        return hash;
    }
}
