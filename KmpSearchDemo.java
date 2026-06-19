import java.util.List;

public final class KmpSearchDemo {
    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private KmpSearchDemo() {
    }

    public static void main(String[] args) {
        String text = args.length > 0 ? args[0] : DEFAULT_TEXT;
        String pattern = args.length > 1 ? args[1] : DEFAULT_PATTERN;

        KmpPattern compiledPattern = KmpSearch.compile(pattern);
        List<Integer> matches = compiledPattern.findMatchesIn(text);

        for (int matchIndex : matches) {
            System.out.print(matchIndex + " ");
        }
    }
}
