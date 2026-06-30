public record SearchResult(boolean found, int index, int insertionPoint) {
    public SearchResult {
        if (found && index < 0) {
            throw new IllegalArgumentException("found results must have a non-negative index");
        }

        if (!found && index != -1) {
            throw new IllegalArgumentException("not-found results must use -1 as the index");
        }

        if (insertionPoint < 0) {
            throw new IllegalArgumentException("insertionPoint must be non-negative");
        }

        if (found && insertionPoint != index) {
            throw new IllegalArgumentException("found results must use the index as the insertion point");
        }
    }

    public static SearchResult found(int index) {
        return new SearchResult(true, index, index);
    }

    public static SearchResult notFound(int insertionPoint) {
        return new SearchResult(false, -1, insertionPoint);
    }

    public int or(int defaultIndex) {
        return found ? index : insertionPoint;
    }
}
