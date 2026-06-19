package kmp;

import java.util.Arrays;
import java.util.List;

public final class KmpMatcherTest {

    private KmpMatcherTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        expectSearch(KmpExamples.SAMPLE_REQUEST, KmpExamples.SAMPLE_MATCHES);
        expectSearch(KmpSearchRequest.of("aa", "aaaa"), Arrays.asList(0, 1, 2));
        expectSearch(KmpSearchRequest.of("needle", "haystack"), Arrays.<Integer>asList());
        expectSearch(KmpSearchRequest.of("", "haystack"), Arrays.<Integer>asList());
        expectSearch(KmpSearchRequest.of("needle", ""), Arrays.<Integer>asList());

        expectMatches("aaba", "aabaacaadaabaaba", Arrays.asList(0, 9, 12));
        expectMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        expectMatches("needle", "haystack", Arrays.<Integer>asList());
        expectMatches("", "haystack", Arrays.<Integer>asList());
        expectMatches("needle", "", Arrays.<Integer>asList());

        expectIllegalArgument(() -> KMPSearch.search((KmpSearchRequest) null));
        expectIllegalArgument(() -> KMPSearch.search(null, "text"));
        expectIllegalArgument(() -> KMPSearch.search("pattern", null));
        expectIllegalArgument(() -> KMPSearch.findAllMatches(null, "text"));
        expectIllegalArgument(() -> KMPSearch.findAllMatches("pattern", null));
        expectIllegalArgument(() -> KmpMatcher.search(null));

        System.out.println("All KMP matcher tests passed.");
    }

    private static void expectMatches(String pattern, String text, List<Integer> expectedMatches) {
        List<Integer> actual = KMPSearch.findAllMatches(pattern, text);
        if (!actual.equals(expectedMatches)) {
            throw new AssertionError("Expected " + expectedMatches + " but got " + actual);
        }
    }

    private static void expectSearch(KmpSearchRequest request, List<Integer> expectedMatches) {
        KmpSearchResult expected = KmpSearchResult.of(request, expectedMatches);
        KmpSearchResult actual = KMPSearch.search(request);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }

        List<Integer> convenienceMatches = KMPSearch.findAllMatches(request.pattern(), request.text());
        if (!convenienceMatches.equals(expectedMatches)) {
            throw new AssertionError("Expected " + expectedMatches + " but got " + convenienceMatches);
        }
    }

    private static void expectIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException.");
    }
}
