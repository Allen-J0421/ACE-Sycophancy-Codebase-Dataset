import java.util.Objects;

public final class SearchApplication {

    private static final String DEFAULT_TEXT = "aabaacaadaabaaba";
    private static final String DEFAULT_PATTERN = "aaba";

    private final SearchEngine searchEngine;

    public SearchApplication(SearchEngine searchEngine) {
        this.searchEngine = Objects.requireNonNull(searchEngine, "searchEngine must not be null");
    }

    public static SearchApplication createDefault() {
        return new SearchApplication(SearchEngines.naive());
    }

    public SearchResult search(SearchRequest request) {
        return searchEngine.search(request);
    }

    public String run(SearchRequest request) {
        return search(request).joinMatchIndexes();
    }

    public String run(String[] args) {
        return run(parseArguments(args));
    }

    public SearchRequest parseArguments(String[] args) {
        Objects.requireNonNull(args, "args must not be null");

        String text = args.length > 0 ? args[0] : DEFAULT_TEXT;
        String pattern = args.length > 1 ? args[1] : DEFAULT_PATTERN;
        return new SearchRequest(text, pattern);
    }
}
