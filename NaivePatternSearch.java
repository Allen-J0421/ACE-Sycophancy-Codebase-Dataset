import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NaivePatternSearch {

    private NaivePatternSearch() {
    }

    public static List<Integer> search(String pattern, String text) {
        return search(new SearchRequest(text, pattern)).matchIndexes();
    }

    public static SearchResult search(SearchRequest request) {
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

    public static String joinMatchIndexes(List<Integer> matches) {
        Objects.requireNonNull(matches, "matches must not be null");

        if (matches.isEmpty()) {
            return "";
        }

        StringBuilder formattedMatches = new StringBuilder();
        for (int index = 0; index < matches.size(); index++) {
            if (index > 0) {
                formattedMatches.append(' ');
            }
            formattedMatches.append(matches.get(index));
        }

        return formattedMatches.toString();
    }

    private static boolean matchesAt(SearchRequest request, int startIndex, int patternLength) {
        for (int offset = 0; offset < patternLength; offset++) {
            if (request.text().charAt(startIndex + offset)
                    != request.pattern().charAt(offset)) {
                return false;
            }
        }

        return true;
    }
}
