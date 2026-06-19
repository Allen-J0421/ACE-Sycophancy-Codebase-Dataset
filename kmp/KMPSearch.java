package kmp;

import java.util.List;

public final class KMPSearch {

    private KMPSearch() {
        // Utility class.
    }

    public static KmpSearchResult search(KmpSearchRequest request) {
        return KmpMatcher.search(request);
    }

    public static KmpSearchResult search(CharSequence pattern, CharSequence text) {
        return search(KmpSearchRequest.of(pattern, text));
    }

    public static List<Integer> findAllMatches(CharSequence pattern, CharSequence text) {
        return search(pattern, text).matches();
    }

    public static void main(String[] args) {
        KmpSearchRequest request = parseRequest(args);
        KmpSearchResult result = search(request);

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
