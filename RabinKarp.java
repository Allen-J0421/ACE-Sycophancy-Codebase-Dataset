import java.util.ArrayList;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int PRIME_MODULUS = 101;

    private RabinKarp() {
        // Utility class.
    }

    static ArrayList<Integer> search(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        int patternLength = pattern.length();
        int textLength = text.length();

        ArrayList<Integer> matches = new ArrayList<>();
        if (patternLength == 0) {
            for (int i = 0; i <= textLength; i++) {
                matches.add(i);
            }
            return matches;
        }
        if (patternLength > textLength) {
            return matches;
        }

        int patternHash = 0;
        int windowHash = 0;
        int highOrderFactor = 1;

        for (int i = 0; i < patternLength - 1; i++) {
            highOrderFactor = (highOrderFactor * RADIX) % PRIME_MODULUS;
        }

        for (int i = 0; i < patternLength; i++) {
            patternHash = (RADIX * patternHash + pattern.charAt(i)) % PRIME_MODULUS;
            windowHash = (RADIX * windowHash + text.charAt(i)) % PRIME_MODULUS;
        }

        for (int offset = 0; offset <= textLength - patternLength; offset++) {
            if (patternHash == windowHash && matchesAt(text, pattern, offset)) {
                matches.add(offset);
            }

            if (offset < textLength - patternLength) {
                windowHash = rollHash(
                        windowHash,
                        text.charAt(offset),
                        text.charAt(offset + patternLength),
                        highOrderFactor);
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

    private static int rollHash(int currentHash, char outgoing, char incoming, int highOrderFactor) {
        int nextHash = RADIX * (currentHash - outgoing * highOrderFactor) + incoming;
        nextHash %= PRIME_MODULUS;
        if (nextHash < 0) {
            nextHash += PRIME_MODULUS;
        }
        return nextHash;
    }

    public static void main(String[] args) {
        String text = "geeksforgeeks";
        String pattern = "geeks";
        ArrayList<Integer> matches = search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
