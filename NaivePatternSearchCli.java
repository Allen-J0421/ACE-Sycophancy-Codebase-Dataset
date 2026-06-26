public final class NaivePatternSearchCli {

    private static final SearchRequest DEFAULT_REQUEST =
            new SearchRequest("aabaacaadaabaaba", "aaba");

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        System.out.println(NaivePatternSearch.format(NaivePatternSearch.search(parseArguments(args))));
    }

    static SearchRequest parseArguments(String[] args) {
        java.util.Objects.requireNonNull(args, "args must not be null");

        String text = args.length > 0 ? args[0] : DEFAULT_REQUEST.text();
        String pattern = args.length > 1 ? args[1] : DEFAULT_REQUEST.pattern();
        return new SearchRequest(text, pattern);
    }

    static SearchRequest defaultRequest() {
        return DEFAULT_REQUEST;
    }
}
