import java.util.List;

public final class NaivePatternSearch {

    private static final SearchApplication APPLICATION = SearchRuntime.application();

    private NaivePatternSearch() {
    }

    public static List<Integer> search(String pattern, String text) {
        return search(new SearchRequest(text, pattern)).matchIndexes();
    }

    public static SearchResult search(SearchRequest request) {
        return APPLICATION.search(request);
    }

    public static String joinMatchIndexes(List<Integer> matches) {
        return SearchResultFormatter.formatMatchIndexes(matches);
    }
}
