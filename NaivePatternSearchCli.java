public final class NaivePatternSearchCli {

    private static final SearchCliSupport CLI_SUPPORT = SearchRuntime.cliSupport();

    private NaivePatternSearchCli() {
    }

    public static void main(String[] args) {
        System.out.println(CLI_SUPPORT.render(args));
    }

    static SearchRequest parseArguments(String[] args) {
        return CLI_SUPPORT.parseArguments(args);
    }
}
