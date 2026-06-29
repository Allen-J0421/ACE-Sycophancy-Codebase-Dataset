final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
