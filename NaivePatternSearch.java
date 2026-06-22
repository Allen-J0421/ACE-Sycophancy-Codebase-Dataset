import java.util.ArrayList;
import java.util.List;

public final class NaivePatternSearch {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearch() {
    }

    static ArrayList<Integer> search(String pattern, String text) {
        ArrayList<Integer> matches = new ArrayList<>();
        int patternLength = pattern.length();
        int textLength = text.length();

        for (int startIndex = 0; startIndex <= textLength - patternLength; startIndex++) {
            if (matchesAt(text, pattern, startIndex)) {
                matches.add(startIndex);
            }
        }

        return matches;
    }

    private static boolean matchesAt(String text, String pattern, int startIndex) {
        for (int patternIndex = 0; patternIndex < pattern.length(); patternIndex++) {
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
        ArrayList<Integer> matches = search(DEFAULT_PATTERN, DEFAULT_TEXT);
        System.out.print(formatMatches(matches));
    }
}
