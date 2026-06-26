import java.util.Objects;

public final class SearchRequestParser {

    private final SearchRequest defaultRequest;

    public SearchRequestParser(SearchRequest defaultRequest) {
        this.defaultRequest = Objects.requireNonNull(defaultRequest, "defaultRequest must not be null");
    }

    public SearchRequest parse(String[] args) {
        Objects.requireNonNull(args, "args must not be null");

        String text = args.length > 0 ? args[0] : defaultRequest.text();
        String pattern = args.length > 1 ? args[1] : defaultRequest.pattern();
        return new SearchRequest(text, pattern);
    }

    public SearchRequest defaultRequest() {
        return defaultRequest;
    }
}
