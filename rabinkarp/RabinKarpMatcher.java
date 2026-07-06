package rabinkarp;

import java.util.ArrayList;
import java.util.List;

public final class RabinKarpMatcher {
    public static final int DEFAULT_RADIX = 256;
    public static final int DEFAULT_MODULUS = 101;

    private final int radix;
    private final int modulus;

    public RabinKarpMatcher(int radix, int modulus) {
        this.radix = radix;
        this.modulus = modulus;
    }

    public List<Integer> search(CharSequence pattern, CharSequence text) {
        CompiledPattern cp = CompiledPattern.compile(pattern, radix, modulus);
        int n = text.length();
        int m = cp.length();
        int txtHash = hashOf(text, m);
        List<Integer> result = allMatchPositions(n - m + 1);
        for (int i = 0; i <= n - m; i++) {
            if (cp.hash() == txtHash && matchesAt(text, cp.pattern(), i)) {
                result.add(i);
            }
            if (i < n - m) {
                txtHash = rollHash(txtHash, text.charAt(i), text.charAt(i + m), cp.highOrderFactor());
            }
        }
        return result;
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

    private List<Integer> allMatchPositions(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }

    private int hashOf(CharSequence seq, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + seq.charAt(i)) % modulus;
        }
        return hash;
    }

    private boolean matchesAt(CharSequence text, CharSequence pattern, int pos) {
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(pos + j) != pattern.charAt(j)) return false;
        }
        return true;
    }

    private int rollHash(int currentHash, char leaving, char entering, int highOrderFactor) {
        int hash = (radix * (currentHash - leaving * highOrderFactor) + entering) % modulus;
        if (hash < 0) hash += modulus;
        return hash;
    }
}
