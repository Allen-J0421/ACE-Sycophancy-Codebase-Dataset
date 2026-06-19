package kmp;

import java.util.Arrays;
import java.util.List;

public final class KmpMatcherTest {

    private static final String SAMPLE_PATTERN = "aaba";
    private static final String SAMPLE_TEXT = "aabaacaadaabaaba";
    private static final List<Integer> SAMPLE_MATCHES = Arrays.asList(0, 9, 12);

    private KmpMatcherTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        expectMatches(SAMPLE_PATTERN, SAMPLE_TEXT, SAMPLE_MATCHES);
        expectMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        expectMatches("needle", "haystack", Arrays.<Integer>asList());
        expectMatches("", "haystack", Arrays.<Integer>asList());
        expectMatches("needle", "", Arrays.<Integer>asList());

        expectIllegalArgument(() -> KMPSearch.findAllMatches(null, "text"));
        expectIllegalArgument(() -> KMPSearch.findAllMatches("pattern", null));
        expectListIsImmutable(KMPSearch.findAllMatches("aa", "aaaa"));

        System.out.println("All KMP matcher tests passed.");
    }

    private static void expectMatches(String pattern, String text, List<Integer> expectedMatches) {
        List<Integer> actual = KMPSearch.findAllMatches(pattern, text);
        if (!actual.equals(expectedMatches)) {
            throw new AssertionError("Expected " + expectedMatches + " but got " + actual);
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

    private static void expectListIsImmutable(List<Integer> matches) {
        try {
            matches.add(42);
        } catch (UnsupportedOperationException expected) {
            return;
        }

        throw new AssertionError("Expected an unmodifiable match list.");
    }
}
