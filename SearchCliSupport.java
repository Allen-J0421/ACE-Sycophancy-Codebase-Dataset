import java.util.Objects;

public final class SearchCliSupport {

    private final SearchApplication application;
    private final SearchRequestParser requestParser;

    public SearchCliSupport(
            SearchApplication application, SearchRequestParser requestParser) {
        this.application = Objects.requireNonNull(application, "application must not be null");
        this.requestParser = Objects.requireNonNull(requestParser, "requestParser must not be null");
    }

    public SearchRequest parseArguments(String[] args) {
        return requestParser.parse(args);
    }

    public SearchResult search(String[] args) {
        return application.search(parseArguments(args));
    }

    public String render(String[] args) {
        return SearchResultFormatter.format(search(args));
    }
}
