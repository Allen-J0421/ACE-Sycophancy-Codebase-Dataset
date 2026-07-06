package rabinkarp;

import stringsearch.MatchResult;
import stringsearch.RollingHashMatcher;
import stringsearch.TextWindow;

public final class RabinKarpMatcher extends RollingHashMatcher {
    public static final int DEFAULT_RADIX = 256;
    public static final int DEFAULT_MODULUS = 101;

    public RabinKarpMatcher(int radix, int modulus) {
        super(new PolynomialHashCalculator(radix, modulus));
    }

    MatchResult search(RabinKarpPattern compiledPat, CharSequence text) {
        int n = text.length();
        int m = compiledPat.length();
        TextWindow window = new TextWindow(text, 0, m);
        int txtHash = hashOf(window, window.length());
        MatchResult result = newResult(n - m + 1);
        while (window.start() <= n - m) {
            if (compiledPat.hash() == txtHash && window.startsWith(compiledPat.pattern())) {
                recordMatch(result, window.start());
            }
            if (window.start() < n - m) {
                txtHash = rollHash(txtHash, window.leaving(), window.entering(), compiledPat.highOrderFactor());
            }
            window = window.slide();
        }
        return result;
    }
}
