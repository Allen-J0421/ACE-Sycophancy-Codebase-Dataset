import java.util.ArrayList;
import java.util.Collections;
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
            return Collections.emptyList();
        }

        return searchNonEmptyPattern(pattern, text);
    }

    private static List<Integer> searchNonEmptyPattern(String pattern, String text) {
        int patternLength = pattern.length();
        int lastStart = text.length() - patternLength;
        int patternHash = RollingHash.hash(pattern, patternLength);
        RollingHash textWindow = RollingHash.from(text, patternLength);
        List<Integer> matches = new ArrayList<>();

        for (int start = 0; start <= lastStart; start++) {
            if (windowMatches(patternHash, textWindow, text, pattern, start)) {
                matches.add(start);
            }

            if (start < lastStart) {
                advanceWindow(textWindow, text, start, patternLength);
            }
        }

        return matches;
    }

    private static boolean windowMatches(
        int patternHash,
        RollingHash textWindow,
        String text,
        String pattern,
        int start
    ) {
        return patternHash == textWindow.value()
            && text.regionMatches(start, pattern, 0, pattern.length());
    }

    private static void advanceWindow(
        RollingHash textWindow,
        String text,
        int start,
        int patternLength
    ) {
        textWindow.roll(text.charAt(start), text.charAt(start + patternLength));
    }

    private static void validateInputs(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }
    }

    private static List<Integer> allMatchPositions(int textLength) {
        List<Integer> positions = new ArrayList<>(textLength + 1);
        for (int index = 0; index <= textLength; index++) {
            positions.add(index);
        }
        return positions;
    }

    private static final class RollingHash {
        private final int highOrderMultiplier;
        private int value;

        private RollingHash(int value, int highOrderMultiplier) {
            this.value = value;
            this.highOrderMultiplier = highOrderMultiplier;
        }

        private static RollingHash from(String text, int windowLength) {
            return new RollingHash(hash(text, windowLength), highOrderMultiplier(windowLength));
        }

        private static int hash(String value, int length) {
            int hash = 0;
            for (int index = 0; index < length; index++) {
                hash = (RADIX * hash + value.charAt(index)) % MODULUS;
            }
            return hash;
        }

        private static int highOrderMultiplier(int windowLength) {
            int multiplier = 1;
            for (int index = 0; index < windowLength - 1; index++) {
                multiplier = (multiplier * RADIX) % MODULUS;
            }
            return multiplier;
        }

        private int value() {
            return value;
        }

        private void roll(char outgoingCharacter, char incomingCharacter) {
            value = positiveModulo(
                RADIX * (value - outgoingCharacter * highOrderMultiplier)
                    + incomingCharacter
            );
        }

        private static int positiveModulo(int value) {
            int result = value % MODULUS;
            return result < 0 ? result + MODULUS : result;
        }
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
