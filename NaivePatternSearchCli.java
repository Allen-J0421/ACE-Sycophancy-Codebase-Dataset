import java.util.List;
import java.util.Objects;

public final class NaivePatternSearchCli {

    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        SearchRequest request = parseArguments(args);
        SearchResult result = NaivePatternSearch.search(request);
        System.out.println(result.joinMatchIndexes());
    }

    static SearchRequest parseArguments(String[] args) {
        Objects.requireNonNull(args, "args must not be null");

        String text = args.length > 0 ? args[0] : DEFAULT_TEXT;
        String pattern = args.length > 1 ? args[1] : DEFAULT_PATTERN;
        return new SearchRequest(text, pattern);
    }
}
