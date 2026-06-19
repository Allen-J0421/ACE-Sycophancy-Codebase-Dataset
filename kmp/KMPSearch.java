package kmp;

import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static void main(String[] args) {
        List<Integer> matches = KmpMatcher.findAllMatches(
            KmpExamples.SAMPLE_PATTERN,
            KmpExamples.SAMPLE_TEXT
        );

        System.out.println(KmpFormatter.joinMatches(matches));
    }
}
