package rabinkarp;

import java.util.ArrayList;
import java.util.List;

public class RollingHashMatcher implements StringMatcher {
    protected final HashCalculator hashCalculator;

    public RollingHashMatcher(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    @Override
    public List<Integer> search(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        int patHash = hashCalculator.hash(pattern, m);
        int h = hashCalculator.highOrderFactor(m);
        TextWindow window = new TextWindow(text, 0, m);
        int txtHash = hashCalculator.hash(window, window.length());
        List<Integer> result = allMatchPositions(n - m + 1);
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

    protected List<Integer> allMatchPositions(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }
}
