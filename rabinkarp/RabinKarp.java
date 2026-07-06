package rabinkarp;

import java.util.List;

public final class RabinKarp {
    private static final StringMatcher DEFAULT_MATCHER;

    static {
        DEFAULT_MATCHER = StringMatcherFactory.rabinKarp();
    }

    private RabinKarp() {}

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return DEFAULT_MATCHER.search(pattern, text);
    }
}
