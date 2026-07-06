package rabinkarp;

import java.util.List;

public final class RabinKarpMatcher extends RollingHashMatcher {
    public static final int DEFAULT_RADIX = 256;
    public static final int DEFAULT_MODULUS = 101;

    public RabinKarpMatcher(int radix, int modulus) {
        super(new PolynomialHashCalculator(radix, modulus));
    }

    List<Integer> search(RabinKarpPattern pattern, CharSequence text) {
        int n = text.length();
        int m = pattern.length();
        int txtHash = hashOf(text, m);
        List<Integer> result = allMatchPositions(n - m + 1);
        for (int i = 0; i <= n - m; i++) {
            if (pattern.hash() == txtHash && matchesAt(text, pattern.pattern(), i)) {
                result.add(i);
            }
            if (i < n - m) {
                txtHash = rollHash(txtHash, text.charAt(i), text.charAt(i + m), pattern.highOrderFactor());
            }
        }
        return result;
    }
}
