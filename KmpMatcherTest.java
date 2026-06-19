import java.util.Arrays;
import java.util.List;

public final class KmpMatcherTest {

    private KmpMatcherTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        expectMatches("aaba", "aabaacaadaabaaba", Arrays.asList(0, 9, 12));
        expectMatches("aa", "aaaa", Arrays.asList(0, 1, 2));
        expectMatches("needle", "haystack", Arrays.<Integer>asList());
        expectMatches("", "haystack", Arrays.<Integer>asList());
        expectMatches("needle", "", Arrays.<Integer>asList());

        expectIllegalArgument(() -> KmpMatcher.findAllMatches(null, "text"));
        expectIllegalArgument(() -> KmpMatcher.findAllMatches("pattern", null));

        System.out.println("All KMP matcher tests passed.");
    }

    private static void expectMatches(String pattern, String text, List<Integer> expected) {
        List<Integer> actual = KmpMatcher.findAllMatches(pattern, text);
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
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
