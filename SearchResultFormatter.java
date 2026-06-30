final class SearchResultFormatter {
    private static final String FOUND_MESSAGE = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private SearchResultFormatter() {
    }

    static String format(SearchResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        if (!result.isFound()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE + result.index();
    }
}
