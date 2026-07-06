package rabinkarp;

import java.util.ArrayList;
import java.util.List;

public abstract class RollingHashMatcher implements StringMatcher {
    protected final int radix;
    protected final int modulus;

    protected RollingHashMatcher(int radix, int modulus) {
        this.radix = radix;
        this.modulus = modulus;
    }

    protected int hashOf(CharSequence seq, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (radix * hash + seq.charAt(i)) % modulus;
        }
        return hash;
    }

    protected int rollHash(int currentHash, char leaving, char entering, int highOrderFactor) {
        int hash = (radix * (currentHash - leaving * highOrderFactor) + entering) % modulus;
        if (hash < 0) hash += modulus;
        return hash;
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
