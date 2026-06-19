package kmp;

import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        return KmpMatcher.findAllMatches(pattern, text);
    }

    public static void main(String[] args) {
        String pattern;
        String text;

        if (args == null || args.length == 0) {
            pattern = KmpExamples.SAMPLE_PATTERN;
            text = KmpExamples.SAMPLE_TEXT;
        } else if (args.length == 2) {
            pattern = args[0];
            text = args[1];
        } else {
            throw new IllegalArgumentException("Usage: java kmp.KMPSearch [pattern text]");
        }

        System.out.println(KmpFormatter.joinMatches(findAllMatches(pattern, text)));
    }
}
