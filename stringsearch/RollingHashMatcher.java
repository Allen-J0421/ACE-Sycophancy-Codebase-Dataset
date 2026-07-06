package stringsearch;

public class RollingHashMatcher implements StringMatcher {
    protected final HashCalculator hashCalculator;

    public RollingHashMatcher(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    @Override
    public MatchResult search(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        int patHash = hashCalculator.hash(pattern, m);
        int h = hashCalculator.highOrderFactor(m);
        TextWindow window = new TextWindow(text, 0, m);
        int txtHash = hashCalculator.hash(window, window.length());
        MatchResult result = new MatchResult(n - m + 1);
        while (window.start() <= n - m) {
            if (patHash == txtHash && window.startsWith(pattern)) {
                result.add(window.start());
            }
            if (window.start() < n - m) {
                txtHash = hashCalculator.roll(txtHash, window.leaving(), window.entering(), h);
            }
            window = window.slide();
        }
        return result;
    }

    protected int hashOf(CharSequence seq, int length) {
        return hashCalculator.hash(seq, length);
    }

    protected int rollHash(int currentHash, char leaving, char entering, int highOrderFactor) {
        return hashCalculator.roll(currentHash, leaving, entering, highOrderFactor);
    }

    /** For subclasses in other packages that cannot access MatchResult's package-private constructor. */
    protected MatchResult newResult() {
        return new MatchResult();
    }

    protected MatchResult newResult(int capacity) {
        return new MatchResult(capacity);
    }

    /** For subclasses in other packages that cannot access MatchResult's package-private add(). */
    protected void recordMatch(MatchResult result, int pos) {
        result.add(pos);
    }
}
