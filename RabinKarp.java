import java.util.List;

public final class RabinKarp {

    private static final RabinKarpMatcher DEFAULT_MATCHER = new RabinKarpMatcher(256, 101);

    private RabinKarp() {
        // Utility class.
    }

    public static List<Integer> search(CharSequence pattern, CharSequence text) {
        return DEFAULT_MATCHER.search(pattern, text);
    }

}
