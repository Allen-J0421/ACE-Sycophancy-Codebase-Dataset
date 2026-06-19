package kmp;

import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static void main(String[] args) {
        KmpSearchRequest request = parseRequest(args);
        KmpSearchResult result = KmpMatcher.search(request);

        System.out.println(KmpFormatter.formatMatches(result));
    }

    private static KmpSearchRequest parseRequest(String[] args) {
        if (args == null || args.length == 0) {
            return KmpExamples.SAMPLE_REQUEST;
        }

        if (args.length == 2) {
            return KmpSearchRequest.of(args[0], args[1]);
        }

        throw new IllegalArgumentException("Usage: java kmp.KMPSearch [pattern text]");
    }
}
