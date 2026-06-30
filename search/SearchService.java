package search;

import java.util.List;

public interface SearchService<T> {
    SearchResult search(List<T> sortedValues, T target);
}
