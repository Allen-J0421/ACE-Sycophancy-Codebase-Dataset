final class SearchResult {
    private static final int NOT_FOUND_INDEX = -1;
    private static final SearchResult NOT_FOUND_RESULT = new SearchResult(NOT_FOUND_INDEX);

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult found(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }

        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return NOT_FOUND_RESULT;
    }

    boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }

    int index() {
        return index;
    }

    int indexOrNotFound() {
        return index;
    }
}
