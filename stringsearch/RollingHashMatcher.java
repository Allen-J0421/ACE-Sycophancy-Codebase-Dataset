package stringsearch;

public class RollingHashMatcher implements StringMatcher {
    protected final HashCalculator hashCalculator;

    public RollingHashMatcher(HashCalculator hashCalculator) {
        this.hashCalculator = hashCalculator;
    }

    @Override
    public MatchResult search(SearchContext ctx) {
        int patHash = hashCalculator.hash(ctx.pattern(), ctx.patternLength());
        int h = hashCalculator.highOrderFactor(ctx.patternLength());
        int txtHash = hashCalculator.hash(ctx.window(), ctx.window().length());
        while (ctx.hasMore()) {
            if (patHash == txtHash && ctx.window().startsWith(ctx.pattern())) {
                ctx.recordMatch();
            }
            if (ctx.canAdvance()) {
                txtHash = hashCalculator.roll(txtHash, ctx.window().leaving(), ctx.window().entering(), h);
            }
            ctx.advance();
        }
        return ctx.result();
    }

    protected int hashOf(CharSequence seq, int length) {
        return hashCalculator.hash(seq, length);
    }

    protected int rollHash(int currentHash, char leaving, char entering, int highOrderFactor) {
        return hashCalculator.roll(currentHash, leaving, entering, highOrderFactor);
    }
}
