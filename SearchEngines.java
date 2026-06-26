public final class SearchEngines {

    private static final SearchEngine NAIVE_SEARCH_ENGINE = new NaiveSearchEngine();

    private SearchEngines() {
    }

    public static SearchEngine naive() {
        return NAIVE_SEARCH_ENGINE;
    }
}
