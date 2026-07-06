package rabinkarp;

public final class NaiveMatcher implements StringMatcher {
    @Override
    public MatchResult search(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        MatchResult result = new MatchResult();
        TextWindow window = new TextWindow(text, 0, m);
        while (window.start() <= n - m) {
            if (window.startsWith(pattern)) result.add(window.start());
            window = window.slide();
        }
        return result;
    }
}
