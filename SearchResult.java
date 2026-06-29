final class SearchResult {
    private static final SearchResult NOT_FOUND = new SearchResult(-1);

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    static SearchResult notFound() {
        return NOT_FOUND;
    }

    boolean wasFound() {
        return index >= 0;
    }

    int index() {
        if (!wasFound()) {
            throw new IllegalStateException("Search result has no index.");
        }

        return index;
    }
}
