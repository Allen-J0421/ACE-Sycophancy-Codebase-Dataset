import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
        // Utility class.
    }

    public static List<Integer> search(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        int patternLength = pattern.length();
        int textLength = text.length();
        List<Integer> matches = new ArrayList<>();

        if (patternLength == 0) {
            for (int index = 0; index <= textLength; index++) {
                matches.add(index);
            }
            return matches;
        }

        if (patternLength > textLength) {
            return matches;
        }

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (matchesAt(pattern, text, start, patternLength)) {
                matches.add(start);
            }
        }

        return matches;
    }

    private static boolean matchesAt(String pattern, String text, int start, int patternLength) {
        for (int offset = 0; offset < patternLength; offset++) {
            if (text.charAt(start + offset) != pattern.charAt(offset)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = search(pattern, text);
        for (int index : matches) {
            System.out.print(index + " ");
        }
    }
}
