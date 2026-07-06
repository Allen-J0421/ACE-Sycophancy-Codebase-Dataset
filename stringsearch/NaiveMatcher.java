package stringsearch;

public final class NaiveMatcher implements StringMatcher {
    @Override
    public MatchResult search(SearchContext ctx) {
        while (ctx.hasMore()) {
            if (ctx.window().startsWith(ctx.pattern())) ctx.recordMatch();
            ctx.advance();
        }
        return ctx.result();
    }
}
