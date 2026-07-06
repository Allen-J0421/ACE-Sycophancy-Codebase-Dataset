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
        int txtHash = hashCalculator.hash(text, m);
        List<Integer> result = allMatchPositions(n - m + 1);
        for (int i = 0; i <= n - m; i++) {
            if (patHash == txtHash && matchesAt(text, pattern, i)) {
                result.add(i);
            }
            if (i < n - m) {
                txtHash = hashCalculator.roll(txtHash, text.charAt(i), text.charAt(i + m), h);
            }
        }
        return result;
    }

    protected int hashOf(CharSequence seq, int length) {
        return hashCalculator.hash(seq, length);
    }

    protected int rollHash(int currentHash, char leaving, char entering, int highOrderFactor) {
        return hashCalculator.roll(currentHash, leaving, entering, highOrderFactor);
    }

    protected boolean matchesAt(CharSequence text, CharSequence pattern, int pos) {
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(pos + j) != pattern.charAt(j)) return false;
        }
        return true;
    }

    protected List<Integer> allMatchPositions(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }
}
