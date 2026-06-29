public final class SearchResult {
    public static final int NOT_FOUND_INDEX = -1;

    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    public static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    public static SearchResult notFound() {
        return new SearchResult(NOT_FOUND_INDEX);
    }

    public int index() {
        return index;
    }

    public boolean isFound() {
        return index != NOT_FOUND_INDEX;
    }
}
