public final class NaivePatternSearchCli {

    private static final SearchApplication APPLICATION = SearchApplication.createDefault();
    private static final SearchRequestParser REQUEST_PARSER = SearchRequestParser.createDefault();

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        SearchRequest request = parseArguments(args);
        System.out.println(APPLICATION.render(request));
    }

    static SearchRequest parseArguments(String[] args) {
        return REQUEST_PARSER.parse(args);
    }
}
