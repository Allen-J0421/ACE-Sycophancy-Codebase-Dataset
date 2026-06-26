package naivepatternsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
        // Utility class.
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(text, "text");

        int patternLength = pattern.length();
        int textLength = text.length();
        if (patternLength > textLength) {
            return List.of();
        }

        int expectedMatches = patternLength == 0 ? textLength + 1 : textLength - patternLength + 1;
        List<Integer> matches = new ArrayList<>(expectedMatches);

        if (patternLength == 0) {
            for (int index = 0; index <= textLength; index++) {
                matches.add(index);
            }
            return List.copyOf(matches);
        }

        for (int start = 0; start <= textLength - patternLength; start++) {
            if (matchesAt(pattern, text, start, patternLength)) {
                matches.add(start);
            }
        }

        return List.copyOf(matches);
    }

    private static boolean matchesAt(CharSequence pattern, CharSequence text, int start, int patternLength) {
        for (int offset = 0; offset < patternLength; offset++) {
            if (text.charAt(start + offset) != pattern.charAt(offset)) {
                return false;
            }
        }
        return true;
    }
}
