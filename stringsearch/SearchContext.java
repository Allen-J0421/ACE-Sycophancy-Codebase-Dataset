package stringsearch;

public final class SearchContext {
    private final CharSequence pattern;
    private final CharSequence text;
    private TextWindow window;
    private final MatchResult result;
    private final int lastWindowStart;

    public SearchContext(CharSequence pattern, CharSequence text) {
        int m = pattern.length();
        int n = text.length();
        this.pattern = pattern;
        this.text = text;
        this.window = new TextWindow(text, 0, m);
        this.result = new MatchResult(Math.max(0, n - m + 1));
        this.lastWindowStart = n - m;
    }

    public CharSequence pattern() { return pattern; }
    public CharSequence text() { return text; }
    public TextWindow window() { return window; }
    public int patternLength() { return pattern.length(); }
    public int textLength() { return text.length(); }

    public boolean hasMore() { return window.start() <= lastWindowStart; }
    public boolean canAdvance() { return window.start() < lastWindowStart; }

    public void advance() { window = window.slide(); }
    public void recordMatch() { result.add(window.start()); }

    public MatchResult result() { return result; }
}
