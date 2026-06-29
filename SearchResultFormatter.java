final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(int index) {
        return index == BinarySearch.NOT_FOUND
                ? "Element is not present in array"
                : "Element is present at index " + index;
    }
}
