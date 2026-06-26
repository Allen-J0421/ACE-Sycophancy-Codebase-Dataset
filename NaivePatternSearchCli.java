public final class NaivePatternSearchCli {

    private static final SearchApplication APPLICATION = SearchApplication.createDefault();

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        System.out.println(APPLICATION.run(args));
    }

    static SearchRequest parseArguments(String[] args) {
        return APPLICATION.parseArguments(args);
    }
}
