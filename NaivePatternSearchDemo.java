import java.util.List;

public final class NaivePatternSearchDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearchDemo() {
    }

    public static void main(String[] args) {
        System.out.print(defaultOutput());
    }

    static String defaultOutput() {
        return MatchFormatter.format(defaultMatches());
    }

    private static List<Integer> defaultMatches() {
        return NaivePatternSearch.findOccurrences(DEFAULT_PATTERN, DEFAULT_TEXT);
    }
}
