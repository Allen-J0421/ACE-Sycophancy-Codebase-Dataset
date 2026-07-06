package stringsearch;

public interface StringMatcher {
    MatchResult search(SearchContext ctx);

    default MatchResult search(CharSequence pattern, CharSequence text) {
        return search(new SearchContext(pattern, text));
    }
}
