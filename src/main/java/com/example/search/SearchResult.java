package com.example.search;

import java.util.Objects;

public final class SearchResult {
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

    public boolean found() {
        return index != BinarySearch.NOT_FOUND;
    }

    public int index() {
        if (!found()) {
            throw new IllegalStateException("Search target was not found");
        }
        return index;
    }

    public int indexOrDefault(int defaultIndex) {
        return found() ? index : defaultIndex;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SearchResult)) {
            return false;
        }
        SearchResult that = (SearchResult) other;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        if (!found()) {
            return "SearchResult{not found}";
        }
        return "SearchResult{index=" + index + "}";
    }
}
