import java.util.ArrayList;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    private RabinKarp() {
    }

    static ArrayList<Integer> search(String pattern, String text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must be non-null.");
        }

        ArrayList<Integer> matches = new ArrayList<>();
        SearchPattern searchPattern = new SearchPattern(pattern);
        SearchState searchState = new SearchState(text, searchPattern);

        if (searchPattern.isEmpty()) {
            for (int i = 0; i <= text.length(); i++) {
                matches.add(i);
            }
            return matches;
        }

        if (!searchState.hasSearchWindow()) {
            return matches;
        }

        while (searchState.hasCurrentWindow()) {
            if (searchState.isMatch()) {
                matches.add(searchState.currentStart());
            }
            searchState.advance();
        }

        return matches;
    }

    private static final class SearchPattern {
        private final String value;
        private final int hash;
        private final int highOrderMultiplier;

        private SearchPattern(String value) {
            this.value = value;
            this.hash = RollingHash.compute(value, value.length());
            this.highOrderMultiplier = computeHighOrderMultiplier(value.length());
        }

        private boolean isEmpty() {
            return value.isEmpty();
        }

        private int length() {
            return value.length();
        }

        private int hash() {
            return hash;
        }

        private int highOrderMultiplier() {
            return highOrderMultiplier;
        }

        private boolean matches(String text, int startIndex) {
            for (int i = 0; i < value.length(); i++) {
                if (text.charAt(startIndex + i) != value.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final class SearchState {
        private final SearchPattern pattern;
        private final TextWindow window;
        private int startIndex;

        private SearchState(String text, SearchPattern pattern) {
            this.pattern = pattern;
            this.window = new TextWindow(text, pattern.length());
        }

        private boolean hasSearchWindow() {
            return window.exists();
        }

        private boolean hasCurrentWindow() {
            return startIndex <= window.lastStartIndex();
        }

        private int currentStart() {
            return startIndex;
        }

        private boolean isMatch() {
            return pattern.hash() == window.hash()
                && pattern.matches(window.text(), startIndex);
        }

        private void advance() {
            if (startIndex < window.lastStartIndex()) {
                window.slide(pattern.highOrderMultiplier(), startIndex);
            }
            startIndex++;
        }
    }

    private static final class TextWindow {
        private final String text;
        private final int windowLength;
        private int hash;

        private TextWindow(String text, int windowLength) {
            this.text = text;
            this.windowLength = windowLength;

            if (exists()) {
                this.hash = RollingHash.compute(text, windowLength);
            }
        }

        private boolean exists() {
            return text.length() >= windowLength;
        }

        private int lastStartIndex() {
            return text.length() - windowLength;
        }

        private int hash() {
            return hash;
        }

        private String text() {
            return text;
        }

        private void slide(int highOrderMultiplier, int startIndex) {
            hash = RollingHash.roll(
                hash,
                text.charAt(startIndex),
                text.charAt(startIndex + windowLength),
                highOrderMultiplier
            );
        }
    }

    private static final class RollingHash {
        private RollingHash() {
        }

        private static int compute(String value, int length) {
            int hash = 0;
            for (int i = 0; i < length; i++) {
                hash = (RADIX * hash + value.charAt(i)) % MODULUS;
            }
            return hash;
        }

        private static int roll(
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
    }

    private static int computeHighOrderMultiplier(int patternLength) {
        int highOrderMultiplier = 1;
        for (int i = 0; i < patternLength - 1; i++) {
            highOrderMultiplier = (highOrderMultiplier * RADIX) % MODULUS;
        }
        return highOrderMultiplier;
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
