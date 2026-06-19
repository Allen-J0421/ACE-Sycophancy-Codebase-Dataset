import java.util.ArrayList;
import java.util.List;

public class RabinKarp implements RabinKarpMatcher {

    private static final int BASE = 256;
    private static final int MODULUS = 101;

    private final String pattern;
    private final int patternHash;
    private final int highPower;

    public RabinKarp(String pattern) {
        this.pattern = pattern;
        this.highPower = computeHighPower(pattern.length());
        this.patternHash = computeHash(pattern, pattern.length());
    }

    @Override
    public List<Integer> findMatches(String text) {
        List<Integer> matches = new ArrayList<>();
        int patternLen = pattern.length();
        int textLen = text.length();

        if (patternLen > textLen) {
            return matches;
        }

        int windowHash = computeHash(text, patternLen);

        for (int i = 0; i <= textLen - patternLen; i++) {
            if (patternHash == windowHash && isExactMatch(text, i)) {
                matches.add(i);
            }
            if (i < textLen - patternLen) {
                windowHash = rollHash(windowHash, text.charAt(i), text.charAt(i + patternLen));
            }
        }

        return matches;
    }

    private static int computeHighPower(int patternLen) {
        int power = 1;
        for (int i = 0; i < patternLen - 1; i++) {
            power = (power * BASE) % MODULUS;
        }
        return power;
    }

    private static int computeHash(String text, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (BASE * hash + text.charAt(i)) % MODULUS;
        }
        return hash;
    }

    private boolean isExactMatch(String text, int startIndex) {
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(startIndex + j) != pattern.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    private int rollHash(int currentHash, char outgoingChar, char incomingChar) {
        int newHash = (BASE * (currentHash - outgoingChar * highPower) + incomingChar) % MODULUS;
        return newHash < 0 ? newHash + MODULUS : newHash;
    }
}
