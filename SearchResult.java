/**
 * Immutable container for binary search results.
 * Provides a type-safe alternative to returning -1 for "not found".
 */
class SearchResult {
    private final int index;
    private final boolean found;

    private SearchResult(int index, boolean found) {
        this.index = index;
        this.found = found;
    }

    static SearchResult found(int index) {
        return new SearchResult(index, true);
    }

    static SearchResult notFound() {
        return new SearchResult(-1, false);
    }

    public int getIndex() {
        return index;
    }

    public boolean isFound() {
        return found;
    }

    public int getIndexOrThrow() {
        if (!found) {
            throw new IllegalStateException("Element not found");
        }
        return index;
    }

    @Override
    public String toString() {
        return found ? "Found at index " + index : "Not found";
    }
}
