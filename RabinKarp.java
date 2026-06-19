import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int PRIME_MODULUS = 101;

    private RabinKarp() {
        // Utility class.
    }

    public static List<Integer> search(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        List<Integer> matches = new ArrayList<>();
        int patternLength = pattern.length();
        int textLength = text.length();
        if (patternLength == 0) {
            for (int i = 0; i <= textLength; i++) {
                matches.add(i);
            }
            return matches;
        }
        if (patternLength > textLength) {
            return matches;
        }

        RollingHash patternHash = RollingHash.forSequence(pattern, patternLength);
        RollingHash windowHash = RollingHash.forSequence(text, patternLength);
        int lastStart = textLength - patternLength;

        for (int offset = 0; offset <= lastStart; offset++) {
            if (patternHash.matches(windowHash) && matchesAt(text, pattern, offset)) {
                matches.add(offset);
            }

            if (offset < lastStart) {
                windowHash.roll(text.charAt(offset), text.charAt(offset + patternLength));
            }
        }

        return matches;
    }

    private static boolean matchesAt(String text, String pattern, int offset) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(offset + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static final class RollingHash {

        private final int highOrderFactor;
        private int hash;

        private RollingHash(int hash, int highOrderFactor) {
            this.hash = hash;
            this.highOrderFactor = highOrderFactor;
        }

        static RollingHash forSequence(CharSequence sequence, int length) {
            int hash = 0;
            int highOrderFactor = 1;

            for (int i = 0; i < length - 1; i++) {
                highOrderFactor = (highOrderFactor * RADIX) % PRIME_MODULUS;
            }

            for (int i = 0; i < length; i++) {
                hash = (RADIX * hash + sequence.charAt(i)) % PRIME_MODULUS;
            }

            return new RollingHash(hash, highOrderFactor);
        }

        void roll(char outgoing, char incoming) {
            int nextHash = RADIX * (hash - outgoing * highOrderFactor) + incoming;
            nextHash %= PRIME_MODULUS;
            if (nextHash < 0) {
                nextHash += PRIME_MODULUS;
            }
            hash = nextHash;
        }

        boolean matches(RollingHash other) {
            return hash == other.hash;
        }
    }

}
