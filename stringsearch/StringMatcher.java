package stringsearch;

public interface StringMatcher {
    MatchResult search(CharSequence pattern, CharSequence text);
}
