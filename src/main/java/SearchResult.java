final class SearchResult {
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
        return new SearchResult(BinarySearch.NOT_FOUND);
    }

    boolean found() {
        return index != BinarySearch.NOT_FOUND;
    }

    int index() {
        if (!found()) {
            throw new IllegalStateException("Search target was not found");
        }
        return index;
    }

    int indexOrDefault(int defaultIndex) {
        return found() ? index : defaultIndex;
    }
}
