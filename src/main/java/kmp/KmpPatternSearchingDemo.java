package kmp;

import java.util.List;

public final class KmpPatternSearchingDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private KmpPatternSearchingDemo() {
    }

    public static void main(String[] args) {
        List<Integer> matches = KMPSearch.search(DEFAULT_PATTERN, DEFAULT_TEXT);

        System.out.println(formatMatches(matches));
    }

    private static String formatMatches(List<Integer> matches) {
        StringBuilder output = new StringBuilder();
        for (int index = 0; index < matches.size(); index++) {
            if (index > 0) {
                output.append(' ');
            }
            output.append(matches.get(index));
        }
        return output.toString();
    }
}
