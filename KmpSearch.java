import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

public final class KmpSearch {

    private KmpSearch() {
        // Utility class.
    }

    /**
     * Returns the zero-based starting indices of every occurrence of {@code pattern} in {@code text}.
     *
     * @param pattern the non-empty pattern to search for
     * @param text the text to search within
     * @return a list of match starting indices in ascending order
     * @throws NullPointerException if {@code pattern} or {@code text} is null
     * @throws IllegalArgumentException if {@code pattern} is empty
     */
    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return compile(pattern).findMatchesIn(text);
    }

    public static KmpMatchResult analyze(CharSequence pattern, CharSequence text) {
        return compile(pattern).analyzeIn(text);
    }

    public static OptionalInt findFirst(CharSequence pattern, CharSequence text) {
        return compile(pattern).findFirstIn(text);
    }

    public static int countMatches(CharSequence pattern, CharSequence text) {
        return compile(pattern).countMatchesIn(text);
    }

    public static boolean contains(CharSequence pattern, CharSequence text) {
        return compile(pattern).occursIn(text);
    }

    public static void forEachMatch(CharSequence pattern, CharSequence text, IntConsumer matchConsumer) {
        compile(pattern).forEachMatchIn(text, matchConsumer);
    }

    public static KmpPattern compile(CharSequence pattern) {
        return KmpPattern.compile(pattern);
    }
}
