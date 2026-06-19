import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    private RabinKarp() {
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        String normalizedPattern = Objects.requireNonNull(pattern, "Pattern must be non-null.")
            .toString();
        String normalizedText = Objects.requireNonNull(text, "Text must be non-null.")
            .toString();

        SearchPattern searchPattern = new SearchPattern(normalizedPattern);

        if (searchPattern.isEmpty()) {
            return allStartIndexes(normalizedText.length());
        }

        if (!searchPattern.canMatch(normalizedText)) {
            return noMatches();
        }

        SearchState searchState = new SearchState(normalizedText, searchPattern);
        return collectMatches(searchState);
    }

    private static List<Integer> collectMatches(SearchState searchState) {
        List<Integer> matches = new ArrayList<>(searchState.windowCount());
        while (searchState.hasCurrentWindow()) {
            if (searchState.isMatch()) {
                matches.add(searchState.currentStart());
            }
            searchState.advance();
        }
        return matches;
    }

    private static List<Integer> noMatches() {
        return new ArrayList<>();
    }

    private static List<Integer> allStartIndexes(int textLength) {
        List<Integer> matches = new ArrayList<>(textLength + 1);
        for (int i = 0; i <= textLength; i++) {
            matches.add(i);
        }
        return matches;
    }

    private static final class SearchPattern {
        private final String value;
        private final int length;
        private final int hash;
        private final int highOrderMultiplier;

        private SearchPattern(String value) {
            this.value = value;
            this.length = value.length();
            this.hash = RollingHash.compute(value, length);
            this.highOrderMultiplier = computeHighOrderMultiplier(length);
        }

        private boolean isEmpty() {
            return length == 0;
        }

        private boolean canMatch(String text) {
            return length <= text.length();
        }

        private int length() {
            return length;
        }

        private int hash() {
            return hash;
        }

        private int highOrderMultiplier() {
            return highOrderMultiplier;
        }

        private boolean matches(String text, int startIndex) {
            for (int i = 0; i < length; i++) {
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

        private boolean hasCurrentWindow() {
            return startIndex <= window.lastStartIndex();
        }

        private int windowCount() {
            return window.lastStartIndex() + 1;
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
            this.hash = RollingHash.compute(text, windowLength);
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
        List<Integer> res = search(pat, txt);
        for (int idx : res) {
            System.out.print(idx + " ");
        }
        System.out.println();
    }
}
