import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    private RabinKarp() {
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        Objects.requireNonNull(pattern, "Pattern must be non-null.");
        Objects.requireNonNull(text, "Text must be non-null.");

        SearchPattern searchPattern = new SearchPattern(pattern);
        SearchState searchState = new SearchState(text, searchPattern);

        if (searchPattern.isEmpty()) {
            return allStartIndexes(text.length());
        }

        if (!searchState.hasSearchWindow()) {
            return Collections.emptyList();
        }

        return collectMatches(searchState);
    }

    private static List<Integer> collectMatches(SearchState searchState) {
        List<Integer> matches = new ArrayList<>();
        while (searchState.hasCurrentWindow()) {
            if (searchState.isMatch()) {
                matches.add(searchState.currentStart());
            }
            searchState.advance();
        }
        return matches;
    }

    private static List<Integer> allStartIndexes(int textLength) {
        List<Integer> matches = new ArrayList<>(textLength + 1);
        for (int i = 0; i <= textLength; i++) {
            matches.add(i);
        }
        return matches;
    }

    private static final class SearchPattern {
        private final CharSequence value;
        private final int hash;
        private final int highOrderMultiplier;

        private SearchPattern(CharSequence value) {
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

        private boolean matches(CharSequence text, int startIndex) {
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

        private SearchState(CharSequence text, SearchPattern pattern) {
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
        private final CharSequence text;
        private final int windowLength;
        private int hash;

        private TextWindow(CharSequence text, int windowLength) {
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

        private CharSequence text() {
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

        private static int compute(CharSequence value, int length) {
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
