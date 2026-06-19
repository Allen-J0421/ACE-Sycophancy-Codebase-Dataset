import java.util.List;
import java.util.OptionalInt;

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
        OptionalInt firstMatch = compiledPattern.findFirstIn(text);

        System.out.println("Matches: " + matches);
        System.out.println("Count: " + compiledPattern.countMatchesIn(text));
        System.out.println("First: " + (firstMatch.isPresent() ? firstMatch.getAsInt() : "none"));

        System.out.print("Streamed: ");
        compiledPattern.forEachMatchIn(text, matchIndex -> System.out.print(matchIndex + " "));
        if (matches.isEmpty()) {
            System.out.print("none");
        }
        System.out.println();
    }
}
