package kmp;

import java.util.List;
import java.util.StringJoiner;

public final class KMPSearch {

    private static final String SAMPLE_PATTERN = "aaba";
    private static final String SAMPLE_TEXT = "aabaacaadaabaaba";

    private KMPSearch() {
        // Utility class.
    }

    public static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        return KmpMatcher.findAllMatches(pattern, text);
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printMatches(findAllMatches(SAMPLE_PATTERN, SAMPLE_TEXT));
            return;
        } else if (args.length == 2) {
            printMatches(findAllMatches(args[0], args[1]));
            return;
        } else {
            throw new IllegalArgumentException("Usage: java kmp.KMPSearch [pattern text]");
        }
    }

    private static void printMatches(List<Integer> matches) {
        if (matches.isEmpty()) {
            System.out.println();
            return;
        }

        StringJoiner joiner = new StringJoiner(" ");
        for (Integer match : matches) {
            joiner.add(String.valueOf(match));
        }
        System.out.println(joiner.toString());
    }
}
