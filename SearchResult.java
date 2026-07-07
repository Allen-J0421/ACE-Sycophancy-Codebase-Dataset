import java.util.Collections;
import java.util.List;

public class SearchResult {

    private final String pattern;
    private final List<Integer> matches;

    public SearchResult(String pattern, List<Integer> matches) {
        this.pattern = pattern;
        this.matches = Collections.unmodifiableList(matches);
    }

    public List<Integer> getMatches() {
        return matches;
    }

    public int count() {
        return matches.size();
    }

    public boolean hasMatches() {
        return !matches.isEmpty();
    }

    @Override
    public String toString() {
        return "SearchResult{pattern=\"" + pattern + "\", matches=" + matches + "}";
    }
}
