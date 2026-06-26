import java.util.List;
import java.util.Objects;

public final class SearchResultFormatter {

    private SearchResultFormatter() {
    }

    public static String format(SearchResult result) {
        Objects.requireNonNull(result, "result must not be null");
        return formatMatchIndexes(result.matchIndexes());
    }

    public static String formatMatchIndexes(List<Integer> matchIndexes) {
        Objects.requireNonNull(matchIndexes, "matchIndexes must not be null");

        if (matchIndexes.isEmpty()) {
            return "";
        }

        StringBuilder formattedMatches = new StringBuilder();
        for (int index = 0; index < matchIndexes.size(); index++) {
            if (index > 0) {
                formattedMatches.append(' ');
            }
            formattedMatches.append(matchIndexes.get(index));
        }

        return formattedMatches.toString();
    }
}
