package kmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class KmpSearchResult {

    private final KmpSearchRequest request;
    private final List<Integer> matches;

    private KmpSearchResult(KmpSearchRequest request, List<Integer> matches) {
        this.request = request;
        this.matches = matches;
    }

    public static KmpSearchResult of(KmpSearchRequest request, List<Integer> matches) {
        if (request == null) {
            throw new IllegalArgumentException("Search request must not be null.");
        }
        if (matches == null) {
            throw new IllegalArgumentException("Match list must not be null.");
        }

        List<Integer> safeMatches = matches.isEmpty()
            ? Collections.<Integer>emptyList()
            : Collections.unmodifiableList(new ArrayList<Integer>(matches));
        return new KmpSearchResult(request, safeMatches);
    }

    public KmpSearchRequest request() {
        return request;
    }

    public List<Integer> matches() {
        return matches;
    }

    public int matchCount() {
        return matches.size();
    }

    public boolean hasMatches() {
        return !matches.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KmpSearchResult)) {
            return false;
        }
        KmpSearchResult that = (KmpSearchResult) other;
        return request.equals(that.request) && matches.equals(that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, matches);
    }

    @Override
    public String toString() {
        return "KmpSearchResult{request=" + request + ", matches=" + matches + "}";
    }
}
