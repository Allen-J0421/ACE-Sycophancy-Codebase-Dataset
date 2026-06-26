public final class NaivePatternSearchCli {

    private static final SearchApplication APPLICATION =
            new SearchApplication(new NaiveSearchEngine());

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        SearchRequest request = parseArguments(args);
        System.out.println(APPLICATION.run(request));
    }

    static SearchRequest parseArguments(String[] args) {
        return APPLICATION.parseArguments(args);
    }
}
