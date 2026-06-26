public final class SearchApplication {

    private final SearchEngine searchEngine;

    public SearchApplication(SearchEngine searchEngine) {
        this.searchEngine = java.util.Objects.requireNonNull(
                searchEngine, "searchEngine must not be null");
    }

    public static SearchApplication createDefault() {
        return new SearchApplication(SearchEngines.naive());
    }

    public SearchResult search(SearchRequest request) {
        return searchEngine.search(request);
    }
}
