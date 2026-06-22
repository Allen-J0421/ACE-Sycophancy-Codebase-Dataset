import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearch() {
    }

    public static List<Integer> findOccurrences(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        int patternLength = pattern.length();
        int textLength = text.length();
        List<Integer> matches = new ArrayList<>();

        for (int startIndex = 0; startIndex <= textLength - patternLength; startIndex++) {
            if (matchesAt(text, pattern, startIndex, patternLength)) {
                matches.add(startIndex);
            }
        }

        return matches;
    }

    static ArrayList<Integer> search(String pattern, String text) {
        return new ArrayList<>(findOccurrences(pattern, text));
    }

    private static boolean matchesAt(String text, String pattern, int startIndex, int patternLength) {
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            if (text.charAt(startIndex + patternIndex) != pattern.charAt(patternIndex)) {
                return false;
            }
        }

        return true;
    }

    private static String formatMatches(List<Integer> matches) {
        StringBuilder output = new StringBuilder();

        for (int match : matches) {
            output.append(match).append(' ');
        }

        return output.toString();
    }

    public static void main(String[] args) {
        List<Integer> matches = findOccurrences(DEFAULT_PATTERN, DEFAULT_TEXT);
        System.out.print(formatMatches(matches));
    }
}
