import java.util.List;
import java.util.Objects;

public final class NaivePatternSearchDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearchDemo() {
    }

    public static void main(String[] args) {
        List<Integer> matches = NaivePatternSearch.findOccurrences(DEFAULT_PATTERN, DEFAULT_TEXT);
        System.out.print(formatMatches(matches));
    }

    static String formatMatches(List<Integer> matches) {
        Objects.requireNonNull(matches, "matches");

        StringBuilder output = new StringBuilder();

        for (int match : matches) {
            output.append(match).append(' ');
        }

        return output.toString();
    }
}
