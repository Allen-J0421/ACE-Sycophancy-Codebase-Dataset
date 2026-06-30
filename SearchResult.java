final class SearchResult {
    static final int NOT_FOUND_INDEX = -1;

    private static final SearchResult NOT_FOUND = new SearchResult(NOT_FOUND_INDEX);

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }

        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return NOT_FOUND;
    }

    boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }

    int index() {
        return index;
    }
}
