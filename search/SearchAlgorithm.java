package search;

import java.util.List;

public interface SearchAlgorithm<T> {
    SearchResult search(List<T> sortedValues, T target);
}
