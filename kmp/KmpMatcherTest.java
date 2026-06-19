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

        expectIllegalArgument(() -> KmpMatcher.findAllMatches(null, "text"));
        expectIllegalArgument(() -> KmpMatcher.findAllMatches("pattern", null));
        expectIllegalArgument(() -> KmpMatcher.search(null));

        System.out.println("All KMP matcher tests passed.");
    }

    private static void expectSearch(KmpSearchRequest request, List<Integer> expectedMatches) {
        KmpSearchResult expected = KmpSearchResult.of(request, expectedMatches);
        KmpSearchResult actual = KmpMatcher.search(request);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }

        List<Integer> convenienceMatches = KmpMatcher.findAllMatches(request.pattern(), request.text());
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
