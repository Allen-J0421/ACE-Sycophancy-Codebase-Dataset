public final class SearchRuntime {

    private static final SearchRequest DEFAULT_REQUEST =
            new SearchRequest("aabaacaadaabaaba", "aaba");
    private static final SearchEngine DEFAULT_ENGINE = new NaiveSearchEngine();
    private static final SearchApplication DEFAULT_APPLICATION =
            new SearchApplication(DEFAULT_ENGINE);
    private static final SearchRequestParser DEFAULT_REQUEST_PARSER =
            new SearchRequestParser(DEFAULT_REQUEST);
    private static final SearchCliSupport DEFAULT_CLI_SUPPORT =
            new SearchCliSupport(DEFAULT_APPLICATION, DEFAULT_REQUEST_PARSER);

    private SearchRuntime() {
    }

    public static SearchRequest defaultRequest() {
        return DEFAULT_REQUEST;
    }

    public static SearchEngine engine() {
        return DEFAULT_ENGINE;
    }

    public static SearchApplication application() {
        return DEFAULT_APPLICATION;
    }

    public static SearchRequestParser requestParser() {
        return DEFAULT_REQUEST_PARSER;
    }

    public static SearchCliSupport cliSupport() {
        return DEFAULT_CLI_SUPPORT;
    }
}
