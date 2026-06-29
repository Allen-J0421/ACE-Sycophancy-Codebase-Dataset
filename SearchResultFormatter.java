public final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    public static String format(final int index) {
        if (index == BinarySearch.NOT_FOUND_INDEX) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
