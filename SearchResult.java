public final class SearchResult {
    private final int index;

    private SearchResult(int index) {
        this.index = index;
    }

    public static SearchResult foundAt(int index) {
        return new SearchResult(index);
    }

    public static SearchResult notFound() {
        return new SearchResult(BinarySearch.NOT_FOUND);
    }

    public int index() {
        return index;
    }

    public boolean isFound() {
        return index != BinarySearch.NOT_FOUND;
    }
}
