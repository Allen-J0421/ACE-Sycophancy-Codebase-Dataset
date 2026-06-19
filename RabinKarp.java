import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int PRIME_MODULUS = 101;

    private RabinKarp() {
        // Utility class.
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        int patternLength = pattern.length();
        int textLength = text.length();

        if (patternLength == 0) {
            return allMatchPositions(textLength);
        }
        if (patternLength > textLength) {
            return List.of();
        }

        int patternHash = hashOf(pattern, patternLength);
        int windowHash = hashOf(text, patternLength);
        int highOrderFactor = highOrderFactorFor(patternLength);
        int lastStart = textLength - patternLength;
        List<Integer> matches = new ArrayList<>();

        for (int offset = 0; offset <= lastStart; offset++) {
            if (patternHash == windowHash && matchesAt(text, pattern, offset)) {
                matches.add(offset);
            }

            if (offset < lastStart) {
                windowHash = rollHash(
                        windowHash,
                        text.charAt(offset),
                        text.charAt(offset + patternLength),
                        highOrderFactor);
            }
        }

        return List.copyOf(matches);
    }

    private static List<Integer> allMatchPositions(int textLength) {
        List<Integer> matches = new ArrayList<>(textLength + 1);
        for (int i = 0; i <= textLength; i++) {
            matches.add(i);
        }
        return List.copyOf(matches);
    }

    private static int hashOf(CharSequence sequence, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (RADIX * hash + sequence.charAt(i)) % PRIME_MODULUS;
        }
        return hash;
    }

    private static int highOrderFactorFor(int length) {
        int factor = 1;
        for (int i = 0; i < length - 1; i++) {
            factor = (factor * RADIX) % PRIME_MODULUS;
        }
        return factor;
    }

    private static boolean matchesAt(CharSequence text, CharSequence pattern, int offset) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(offset + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static int rollHash(int currentHash, char outgoing, char incoming, int highOrderFactor) {
        int nextHash = RADIX * (currentHash - outgoing * highOrderFactor) + incoming;
        nextHash %= PRIME_MODULUS;
        if (nextHash < 0) {
            nextHash += PRIME_MODULUS;
        }
        return nextHash;
    }

}
