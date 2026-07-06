package rabinkarp;

import stringsearch.MatchResult;
import stringsearch.NaiveMatcher;
import stringsearch.SearchContext;
import stringsearch.StringMatcher;

public enum SearchStrategy implements StringMatcher {
    RABIN_KARP(new RabinKarpMatcher(RabinKarpMatcher.DEFAULT_RADIX, RabinKarpMatcher.DEFAULT_MODULUS)),
    NAIVE(new NaiveMatcher());

    private final StringMatcher impl;

    SearchStrategy(StringMatcher impl) {
        this.impl = impl;
    }

    @Override
    public MatchResult search(SearchContext ctx) {
        return impl.search(ctx);
    }
}
