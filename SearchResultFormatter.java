final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(SearchResult result) {
        return result.isFound()
                ? "Element is present at index " + result.index()
                : "Element is not present in array";
    }
}
