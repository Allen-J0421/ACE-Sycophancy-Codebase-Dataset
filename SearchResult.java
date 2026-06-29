public final class SearchResult {
    private static final SearchResult NOT_FOUND = new SearchResult(-1);

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    public static SearchResult fromIndex(int index) {
        if (index < 0) {
            return notFound();
        }

        return foundAt(index);
    }

    public static SearchResult foundAt(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Result index must be non-negative.");
        }

        return new SearchResult(index);
    }

    public static SearchResult notFound() {
        return NOT_FOUND;
    }

    public boolean isFound() {
        return index >= 0;
    }

    public int index() {
        if (!isFound()) {
            throw new IllegalStateException("Search result has no index.");
        }

        return index;
    }
}
