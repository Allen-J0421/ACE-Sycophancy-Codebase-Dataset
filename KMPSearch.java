import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static void main(String[] args) {
        String text = "aabaacaadaabaaba";
        String pattern = "aaba";

        List<Integer> matches = KmpMatcher.findAllMatches(pattern, text);
        System.out.println(KmpFormatter.joinMatches(matches));
    }
}

final class KmpMatcher {

    private KmpMatcher() {
        // Utility class.
    }

    static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        if (pattern == null || text == null) {
            throw new IllegalArgumentException("Pattern and text must not be null.");
        }
        if (pattern.length() == 0 || text.length() == 0 || pattern.length() > text.length()) {
            return Collections.emptyList();
        }

        int[] lps = buildLps(pattern);
        List<Integer> matches = new ArrayList<>();
        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == pattern.length()) {
                    matches.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex != 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }

        return Collections.unmodifiableList(matches);
    }

    private static int[] buildLps(CharSequence pattern) {
        int[] lps = new int[pattern.length()];
        int prefixLength = 0;

        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(prefixLength)) {
                lps[i++] = ++prefixLength;
            } else if (prefixLength != 0) {
                prefixLength = lps[prefixLength - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }
}

final class KmpFormatter {

    private KmpFormatter() {
        // Utility class.
    }

    static String joinMatches(List<Integer> matches) {
        if (matches.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(matches.get(i));
        }
        return builder.toString();
    }
}
