import java.util.List;
import java.util.Objects;

public record SearchResult(SearchRequest request, List<Integer> matchIndexes) {

    public SearchResult {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(matchIndexes, "matchIndexes must not be null");
        matchIndexes = List.copyOf(matchIndexes);
    }

    public boolean hasMatches() {
        return !matchIndexes.isEmpty();
    }
}
