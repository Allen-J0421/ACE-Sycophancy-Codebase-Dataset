import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RabinKarp {
    private static final String DEMO_TEXT = "geeksforgeeks";
    private static final String DEMO_PATTERN = "geeks";

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
        SearchPattern searchPattern = SearchPattern.from(pattern);
        int patternLength = searchPattern.length();
        int lastStart = text.length() - patternLength;
        TextWindow textWindow = TextWindow.from(text, patternLength);
        List<Integer> matches = new ArrayList<>();

        for (int start = 0; start <= lastStart; start++) {
            if (searchPattern.matches(textWindow, start)) {
                matches.add(start);
            }

            if (start < lastStart) {
                textWindow.advanceFrom(start);
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
        List<Integer> positions = new ArrayList<>(textLength + 1);
        for (int index = 0; index <= textLength; index++) {
            positions.add(index);
        }
        return positions;
    }

    private static final class SearchPattern {
        private final String value;
        private final int hash;

        private SearchPattern(String value, int hash) {
            this.value = value;
            this.hash = hash;
        }

        private static SearchPattern from(String pattern) {
            return new SearchPattern(pattern, RollingHash.hash(pattern, pattern.length()));
        }

        private int length() {
            return value.length();
        }

        private boolean matches(TextWindow textWindow, int start) {
            return hash == textWindow.hash()
                && textWindow.matches(value, start);
        }
    }

    private static final class TextWindow {
        private final String text;
        private final int length;
        private final RollingHash rollingHash;

        private TextWindow(String text, int length, RollingHash rollingHash) {
            this.text = text;
            this.length = length;
            this.rollingHash = rollingHash;
        }

        private static TextWindow from(String text, int length) {
            return new TextWindow(text, length, RollingHash.from(text, length));
        }

        private int hash() {
            return rollingHash.value();
        }

        private boolean matches(String pattern, int start) {
            return text.regionMatches(start, pattern, 0, length);
        }

        private void advanceFrom(int start) {
            rollingHash.roll(text.charAt(start), text.charAt(start + length));
        }
    }

    private static final class RollingHash {
        private static final int RADIX = 256;
        private static final int MODULUS = 101;

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
        printMatches(search(DEMO_PATTERN, DEMO_TEXT));
    }

    private static void printMatches(List<Integer> matches) {
        for (int index : matches) {
            System.out.print(index + " ");
        }
        System.out.println();
    }
}
