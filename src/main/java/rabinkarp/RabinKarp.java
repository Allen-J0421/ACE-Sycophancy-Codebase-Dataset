package rabinkarp;

import java.util.List;

public final class RabinKarp {

    private static final RabinKarpMatcher DEFAULT_MATCHER =
            new RabinKarpMatcher(RabinKarpMatcher.DEFAULT_RADIX, RabinKarpMatcher.DEFAULT_MODULUS);

    private RabinKarp() {
        // Utility class.
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return DEFAULT_MATCHER.search(pattern, text);
    }

}
