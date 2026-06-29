final class SearchResultFormatter {
    private static final String NOT_PRESENT_MESSAGE = "Element is not present in array";
    private static final String PRESENT_MESSAGE_PREFIX = "Element is present at index ";

    private SearchResultFormatter() {
    }

    static String format(SearchResult result) {
        return result.isFound()
                ? PRESENT_MESSAGE_PREFIX + result.index()
                : NOT_PRESENT_MESSAGE;
    }
}
