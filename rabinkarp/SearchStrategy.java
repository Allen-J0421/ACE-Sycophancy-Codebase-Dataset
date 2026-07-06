package rabinkarp;

import stringsearch.MatchResult;
import stringsearch.NaiveMatcher;
import stringsearch.StringMatcher;

public enum SearchStrategy implements StringMatcher {
    RABIN_KARP(new RabinKarpMatcher(RabinKarpMatcher.DEFAULT_RADIX, RabinKarpMatcher.DEFAULT_MODULUS)),
    NAIVE(new NaiveMatcher());

    private final StringMatcher impl;

    SearchStrategy(StringMatcher impl) {
        this.impl = impl;
    }

    @Override
    public MatchResult search(CharSequence pattern, CharSequence text) {
        return impl.search(pattern, text);
    }
}
