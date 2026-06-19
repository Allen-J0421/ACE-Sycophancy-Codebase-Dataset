import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

/**
 * Immutable compiled representation of a pattern for Knuth-Morris-Pratt searches.
 */
public final class KmpPattern {
    private final String value;
    private final int[] longestPrefixSuffix;

    private KmpPattern(String value, int[] longestPrefixSuffix) {
        this.value = value;
        this.longestPrefixSuffix = longestPrefixSuffix;
    }

    public static KmpPattern compile(CharSequence pattern) {
        Objects.requireNonNull(pattern, "pattern must not be null");

        String patternValue = pattern.toString();

        if (patternValue.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }

        return new KmpPattern(patternValue, buildLongestPrefixSuffixTable(patternValue));
    }

    public String value() {
        return value;
    }

    public List<Integer> findMatchesIn(CharSequence text) {
        validateText(text);

        if (value.length() > text.length()) {
            return List.of();
        }

        List<Integer> matches = new ArrayList<>();
        scanMatches(text, matchIndex -> {
            matches.add(matchIndex);
            return true;
        });
        return List.copyOf(matches);
    }

    public OptionalInt findFirstIn(CharSequence text) {
        validateText(text);

        if (value.length() > text.length()) {
            return OptionalInt.empty();
        }

        int[] firstMatchIndex = { -1 };
        scanMatches(text, matchIndex -> {
            firstMatchIndex[0] = matchIndex;
            return false;
        });
        return firstMatchIndex[0] >= 0 ? OptionalInt.of(firstMatchIndex[0]) : OptionalInt.empty();
    }

    public int countMatchesIn(CharSequence text) {
        validateText(text);

        if (value.length() > text.length()) {
            return 0;
        }

        int[] matchCount = { 0 };
        scanMatches(text, matchIndex -> {
            matchCount[0]++;
            return true;
        });
        return matchCount[0];
    }

    public boolean occursIn(CharSequence text) {
        return findFirstIn(text).isPresent();
    }

    public void forEachMatchIn(CharSequence text, IntConsumer matchConsumer) {
        validateText(text);
        Objects.requireNonNull(matchConsumer, "matchConsumer must not be null");

        if (value.length() > text.length()) {
            return;
        }

        scanMatches(text, matchIndex -> {
            matchConsumer.accept(matchIndex);
            return true;
        });
    }

    static int[] buildLongestPrefixSuffixTable(String pattern) {
        int[] table = new int[pattern.length()];
        int prefixLength = 0;
        int currentIndex = 1;

        while (currentIndex < pattern.length()) {
            if (pattern.charAt(currentIndex) == pattern.charAt(prefixLength)) {
                table[currentIndex] = ++prefixLength;
                currentIndex++;
                continue;
            }

            if (prefixLength > 0) {
                prefixLength = table[prefixLength - 1];
            } else {
                table[currentIndex] = 0;
                currentIndex++;
            }
        }

        return table;
    }

    private void validateText(CharSequence text) {
        Objects.requireNonNull(text, "text must not be null");
    }

    private void scanMatches(CharSequence text, MatchVisitor visitor) {
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == value.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == value.length()) {
                    int matchIndex = textIndex - patternIndex;
                    patternIndex = longestPrefixSuffix[patternIndex - 1];

                    if (!visitor.visit(matchIndex)) {
                        return;
                    }
                }
                continue;
            }

            if (patternIndex > 0) {
                patternIndex = longestPrefixSuffix[patternIndex - 1];
            } else {
                textIndex++;
            }
        }
    }

    @FunctionalInterface
    private interface MatchVisitor {
        boolean visit(int matchIndex);
    }
}
