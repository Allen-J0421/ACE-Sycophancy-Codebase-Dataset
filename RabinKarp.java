import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RabinKarp {

    private static final int RADIX = 256;
    private static final int MODULUS = 101;

    private RabinKarp() {
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return SearchInput.of(pattern, text).findMatches();
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

    private static final class SearchInput {
        private final String text;
        private final SearchPattern pattern;

        private SearchInput(String text, SearchPattern pattern) {
            this.text = text;
            this.pattern = pattern;
        }

        private static SearchInput of(CharSequence pattern, CharSequence text) {
            String normalizedPattern = Objects.requireNonNull(
                pattern,
                "Pattern must be non-null."
            ).toString();
            String normalizedText = Objects.requireNonNull(
                text,
                "Text must be non-null."
            ).toString();

            return new SearchInput(normalizedText, new SearchPattern(normalizedPattern));
        }

        private boolean hasEmptyPattern() {
            return pattern.isEmpty();
        }

        private boolean hasMatchableWindow() {
            return pattern.length() <= text.length();
        }

        private int textLength() {
            return text.length();
        }

        private List<Integer> findMatches() {
            if (hasEmptyPattern()) {
                return allStartIndexes(textLength());
            }

            if (!hasMatchableWindow()) {
                return noMatches();
            }

            return collectMatches();
        }

        private List<Integer> collectMatches() {
            TextWindow window = new TextWindow(text, pattern.rollingHash());
            List<Integer> matches = new ArrayList<>(window.windowCount());
            int startIndex = 0;

            while (startIndex <= window.lastStartIndex()) {
                if (pattern.hash() == window.hash() && pattern.matches(text, startIndex)) {
                    matches.add(startIndex);
                }

                if (startIndex < window.lastStartIndex()) {
                    window.slide(startIndex);
                }

                startIndex++;
            }

            return matches;
        }
    }

    private static final class SearchPattern {
        private final String value;
        private final int length;
        private final RollingHash rollingHash;
        private final int hash;

        private SearchPattern(String value) {
            this.value = value;
            this.length = value.length();
            this.rollingHash = new RollingHash(length);
            this.hash = rollingHash.hash(value);
        }

        private boolean isEmpty() {
            return length == 0;
        }

        private int length() {
            return length;
        }

        private int hash() {
            return hash;
        }

        private RollingHash rollingHash() {
            return rollingHash;
        }

        private boolean matches(String text, int startIndex) {
            return text.startsWith(value, startIndex);
        }
    }

    private static final class TextWindow {
        private final String text;
        private final int lastStartIndex;
        private final RollingHash rollingHash;
        private int hash;

        private TextWindow(String text, RollingHash rollingHash) {
            this.text = text;
            this.rollingHash = rollingHash;
            this.lastStartIndex = text.length() - rollingHash.windowLength();
            this.hash = rollingHash.hash(text);
        }

        private int lastStartIndex() {
            return lastStartIndex;
        }

        private int windowCount() {
            return lastStartIndex + 1;
        }

        private int hash() {
            return hash;
        }

        private void slide(int startIndex) {
            hash = rollingHash.roll(hash, text, startIndex);
        }
    }

    private static final class RollingHash {
        private final int windowLength;
        private final int highOrderMultiplier;

        private RollingHash(int windowLength) {
            this.windowLength = windowLength;
            this.highOrderMultiplier = computeHighOrderMultiplier(windowLength);
        }

        private int windowLength() {
            return windowLength;
        }

        private int hash(String value) {
            int hash = 0;
            for (int i = 0; i < windowLength; i++) {
                hash = (RADIX * hash + value.charAt(i)) % MODULUS;
            }
            return hash;
        }

        private int roll(int currentHash, String text, int startIndex) {
            int nextHash = (
                RADIX * (currentHash - text.charAt(startIndex) * highOrderMultiplier)
                    + text.charAt(startIndex + windowLength)
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
