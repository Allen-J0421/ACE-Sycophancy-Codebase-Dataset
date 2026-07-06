package rabinkarp;

public final class RabinKarp {
    private static final StringMatcher DEFAULT_MATCHER;

    static {
        DEFAULT_MATCHER = SearchStrategy.RABIN_KARP;
    }

    private RabinKarp() {}

    public static MatchResult search(CharSequence pattern, CharSequence text) {
        return DEFAULT_MATCHER.search(pattern, text);
    }
}
