import java.util.List;
import java.util.Objects;

public final class NaivePatternSearchCli {

    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        SearchInput input = parseArguments(args);
        List<Integer> matches = NaivePatternSearch.search(input.pattern(), input.text());
        System.out.println(NaivePatternSearch.joinMatchIndexes(matches));
    }

    static SearchInput parseArguments(String[] args) {
        Objects.requireNonNull(args, "args must not be null");

        String text = args.length > 0 ? args[0] : DEFAULT_TEXT;
        String pattern = args.length > 1 ? args[1] : DEFAULT_PATTERN;
        return new SearchInput(text, pattern);
    }

    record SearchInput(String text, String pattern) {
        SearchInput {
            Objects.requireNonNull(text, "text must not be null");
            Objects.requireNonNull(pattern, "pattern must not be null");
        }
    }
}
