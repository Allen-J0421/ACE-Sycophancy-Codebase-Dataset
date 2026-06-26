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
        int maxStart = textLength - patternLength;
        if (maxStart < 0) {
            return List.of();
        }

        List<Integer> matches = new ArrayList<>(maxStart + 1);
        for (int start = 0; start <= maxStart; start++) {
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
