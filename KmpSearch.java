import java.util.List;

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
    public static List<Integer> search(String pattern, String text) {
        return compile(pattern).findMatchesIn(text);
    }

    public static KmpPattern compile(String pattern) {
        return KmpPattern.compile(pattern);
    }
}
