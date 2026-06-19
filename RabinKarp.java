import java.util.ArrayList;

public class RabinKarp {

    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    static ArrayList<Integer> search(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must be non-null.");
        }

        int patternLength = pattern.length();
        int textLength = text.length();
        ArrayList<Integer> matches = new ArrayList<>();

        if (patternLength == 0) {
            for (int i = 0; i <= textLength; i++) {
                matches.add(i);
            }
            return matches;
        }

        if (textLength < patternLength) {
            return matches;
        }

        int highOrderMultiplier = computeHighOrderMultiplier(patternLength);
        int patternHash = computeHash(pattern, patternLength);
        int windowHash = computeHash(text, patternLength);

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (patternHash == windowHash && matchesAt(text, pattern, start)) {
                matches.add(start);
            }

            if (start < textLength - patternLength) {
                windowHash = rollHash(
                    windowHash,
                    text.charAt(start),
                    text.charAt(start + patternLength),
                    highOrderMultiplier
                );
            }
        }

        return matches;
    }

    private static int computeHighOrderMultiplier(int patternLength) {
        int highOrderMultiplier = 1;
        for (int i = 0; i < patternLength - 1; i++) {
            highOrderMultiplier = (highOrderMultiplier * RADIX) % MODULUS;
        }
        return highOrderMultiplier;
    }

    private static int computeHash(String value, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (RADIX * hash + value.charAt(i)) % MODULUS;
        }
        return hash;
    }

    private static boolean matchesAt(String text, String pattern, int startIndex) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(startIndex + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static int rollHash(
        int currentHash,
        char outgoingChar,
        char incomingChar,
        int highOrderMultiplier
    ) {
        int nextHash = (
            RADIX * (currentHash - outgoingChar * highOrderMultiplier) + incomingChar
        ) % MODULUS;

        if (nextHash < 0) {
            nextHash += MODULUS;
        }

        return nextHash;
    }

    public static void main(String[] args) {
        String txt = "geeksforgeeks";
        String pat = "geeks";
        ArrayList<Integer> res = search(pat, txt);
        for (int idx : res) {
            System.out.print(idx + " ");
        }
        System.out.println();
    }
}
