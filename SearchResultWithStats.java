/**
 * Search result with performance statistics.
 * Extends SearchResult with metrics about the search operation.
 */
class SearchResultWithStats extends SearchResult {
    private final SearchStats stats;

    SearchResultWithStats(SearchResult result, SearchStats stats) {
        super(result.getIndex(), result.isFound());
        this.stats = stats;
    }

    public SearchStats getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + stats + ")";
    }
}
