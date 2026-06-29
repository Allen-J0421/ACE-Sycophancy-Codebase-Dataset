final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(SearchResult result) {
        if (!result.wasFound()) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result.index();
    }
}
