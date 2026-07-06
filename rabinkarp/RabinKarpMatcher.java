package rabinkarp;

import stringsearch.MatchResult;
import stringsearch.RollingHashMatcher;
import stringsearch.SearchContext;

public final class RabinKarpMatcher extends RollingHashMatcher {
    public static final int DEFAULT_RADIX = 256;
    public static final int DEFAULT_MODULUS = 101;

    public RabinKarpMatcher(int radix, int modulus) {
        super(new PolynomialHashCalculator(radix, modulus));
    }

    MatchResult search(RabinKarpPattern compiledPat, CharSequence text) {
        SearchContext ctx = new SearchContext(compiledPat.pattern(), text);
        int txtHash = hashOf(ctx.window(), ctx.window().length());
        while (ctx.hasMore()) {
            if (compiledPat.hash() == txtHash && ctx.window().startsWith(ctx.pattern())) {
                ctx.recordMatch();
            }
            if (ctx.canAdvance()) {
                txtHash = rollHash(txtHash, ctx.window().leaving(), ctx.window().entering(), compiledPat.highOrderFactor());
            }
            ctx.advance();
        }
        return ctx.result();
    }
}
