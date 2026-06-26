import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
    }

    static List<Integer> search(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Objects.requireNonNull(text, "text must not be null");

        int patternLength = pattern.length();
        int textLength = text.length();
        List<Integer> matchIndexes = new ArrayList<>();

        for (int startIndex = 0; startIndex <= textLength - patternLength; startIndex++) {
            if (matchesAt(pattern, text, startIndex, patternLength)) {
                matchIndexes.add(startIndex);
            }
        }

        return matchIndexes;
    }

    private static boolean matchesAt(
            String pattern, String text, int startIndex, int patternLength) {
        for (int offset = 0; offset < patternLength; offset++) {
            if (text.charAt(startIndex + offset) != pattern.charAt(offset)) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = search(pattern, text);
        for (int matchIndex : matches) {
            System.out.print(matchIndex + " ");
        }
    }
}
