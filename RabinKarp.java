import java.util.ArrayList;
import java.util.List;

public class RabinKarp implements RabinKarpMatcher {

    private static final int BASE = 256;
    private static final int MODULUS = 101;

    private final String pattern;
    private final int patternHash;
    private final RollingHash rollingHash;

    public RabinKarp(String pattern) {
        this.pattern = pattern;
        this.rollingHash = new RollingHash(BASE, MODULUS, pattern.length());
        this.patternHash = rollingHash.compute(pattern);
    }

    @Override
    public List<Integer> findMatches(String text) {
        List<Integer> matches = new ArrayList<>();
        int patternLen = pattern.length();
        int textLen = text.length();

        if (patternLen > textLen) {
            return matches;
        }

        int windowHash = rollingHash.compute(text, patternLen);

        for (int i = 0; i <= textLen - patternLen; i++) {
            if (patternHash == windowHash && isExactMatch(text, i)) {
                matches.add(i);
            }
            if (i < textLen - patternLen) {
                windowHash = rollingHash.roll(windowHash, text.charAt(i), text.charAt(i + patternLen));
            }
        }

        return matches;
    }

    private boolean isExactMatch(String text, int startIndex) {
        return text.regionMatches(startIndex, pattern, 0, pattern.length());
    }

    private static class RollingHash {
        private final int base;
        private final int modulus;
        private final int highPower;

        RollingHash(int base, int modulus, int windowSize) {
            this.base = base;
            this.modulus = modulus;
            int power = 1;
            for (int i = 0; i < windowSize - 1; i++) {
                power = (power * base) % modulus;
            }
            this.highPower = power;
        }

        int compute(String text) {
            return compute(text, text.length());
        }

        int compute(String text, int length) {
            int hash = 0;
            for (int i = 0; i < length; i++) {
                hash = (base * hash + text.charAt(i)) % modulus;
            }
            return hash;
        }

        int roll(int currentHash, char outgoingChar, char incomingChar) {
            int newHash = (base * (currentHash - outgoingChar * highPower) + incomingChar) % modulus;
            return newHash < 0 ? newHash + modulus : newHash;
        }
    }
}
