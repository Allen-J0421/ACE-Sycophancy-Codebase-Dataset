import java.util.ArrayList;
import java.util.List;

public final class RabinKarp {
    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    private RabinKarp() {
    }

    public static List<Integer> search(String pattern, String text) {
        validateInputs(pattern, text);

        int patternLength = pattern.length();
        int textLength = text.length();

        if (patternLength == 0) {
            return allMatchPositions(textLength);
        }

        if (patternLength > textLength) {
            return new ArrayList<>();
        }

        int patternHash = hash(pattern, patternLength);
        int textHash = hash(text, patternLength);
        int highOrderMultiplier = highOrderMultiplier(patternLength);
        List<Integer> matches = new ArrayList<>();

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (patternHash == textHash && matchesAt(text, pattern, start)) {
                matches.add(start);
            }

            if (start < textLength - patternLength) {
                textHash = rollHash(
                    textHash,
                    text.charAt(start),
                    text.charAt(start + patternLength),
                    highOrderMultiplier
                );
            }
        }

        return matches;
    }

    private static void validateInputs(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }
    }

    private static List<Integer> allMatchPositions(int textLength) {
        List<Integer> positions = new ArrayList<>();
        for (int index = 0; index <= textLength; index++) {
            positions.add(index);
        }
        return positions;
    }

    private static int hash(String value, int length) {
        int hash = 0;
        for (int index = 0; index < length; index++) {
            hash = (RADIX * hash + value.charAt(index)) % MODULUS;
        }
        return hash;
    }

    private static int highOrderMultiplier(int patternLength) {
        int multiplier = 1;
        for (int index = 0; index < patternLength - 1; index++) {
            multiplier = (multiplier * RADIX) % MODULUS;
        }
        return multiplier;
    }

    private static boolean matchesAt(String text, String pattern, int start) {
        for (int offset = 0; offset < pattern.length(); offset++) {
            if (text.charAt(start + offset) != pattern.charAt(offset)) {
                return false;
            }
        }
        return true;
    }

    private static int rollHash(
        int currentHash,
        char outgoingCharacter,
        char incomingCharacter,
        int highOrderMultiplier
    ) {
        int nextHash = (
            RADIX * (currentHash - outgoingCharacter * highOrderMultiplier)
                + incomingCharacter
        ) % MODULUS;

        if (nextHash < 0) {
            nextHash += MODULUS;
        }

        return nextHash;
    }

    public static void main(String[] args) {
        String text = "geeksforgeeks";
        String pattern = "geeks";

        for (int index : search(pattern, text)) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
