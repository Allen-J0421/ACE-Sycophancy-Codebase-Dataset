import java.util.List;
import java.util.Objects;

public final class NaivePatternSearchDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearchDemo() {
    }

    public static void main(String[] args) {
        System.out.print(defaultOutput());
    }

    static String defaultOutput() {
        return formatMatches(defaultMatches());
    }

    private static List<Integer> defaultMatches() {
        return NaivePatternSearch.findOccurrences(DEFAULT_PATTERN, DEFAULT_TEXT);
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
