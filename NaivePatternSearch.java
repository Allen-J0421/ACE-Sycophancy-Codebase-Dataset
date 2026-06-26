import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
    }

    public static List<Integer> search(String pattern, String text) {
        Objects.requireNonNull(pattern, "pattern must not be null");
        Objects.requireNonNull(text, "text must not be null");

        int patternLength = pattern.length();
        int textLength = text.length();

        if (patternLength > textLength) {
            return List.of();
        }

        List<Integer> matchIndexes = new ArrayList<>();

        for (int startIndex = 0; startIndex <= textLength - patternLength; startIndex++) {
            if (matchesAt(pattern, text, startIndex, patternLength)) {
                matchIndexes.add(startIndex);
            }
        }

        return matchIndexes;
    }

    public static String joinMatchIndexes(List<Integer> matches) {
        Objects.requireNonNull(matches, "matches must not be null");

        if (matches.isEmpty()) {
            return "";
        }

        StringBuilder formattedMatches = new StringBuilder();
        for (int index = 0; index < matches.size(); index++) {
            if (index > 0) {
                formattedMatches.append(' ');
            }
            formattedMatches.append(matches.get(index));
        }

        return formattedMatches.toString();
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
}
