public final class SearchResultFormatter {
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private SearchResultFormatter() {
    }

    public static String format(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
