import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaiveSearchEngine implements SearchEngine {

    @Override
    public SearchResult search(SearchRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        if (request.patternIsLongerThanText()) {
            return new SearchResult(request, List.of());
        }

        List<Integer> matchIndexes = new ArrayList<>();
        int patternLength = request.patternLength();

        for (int startIndex = 0;
                startIndex <= request.textLength() - patternLength;
                startIndex++) {
            if (matchesAt(request, startIndex, patternLength)) {
                matchIndexes.add(startIndex);
            }
        }

        return new SearchResult(request, matchIndexes);
    }

    private boolean matchesAt(SearchRequest request, int startIndex, int patternLength) {
        for (int offset = 0; offset < patternLength; offset++) {
            if (request.text().charAt(startIndex + offset)
                    != request.pattern().charAt(offset)) {
                return false;
            }
        }

        return true;
    }
}
