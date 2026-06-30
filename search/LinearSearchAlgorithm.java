package search;

import java.util.List;
import java.util.Objects;

public final class LinearSearchAlgorithm<T> implements SearchAlgorithm<T> {
    @Override
    public SearchResult search(List<T> values, T target) {
        Objects.requireNonNull(values, "values must not be null");

        for (int index = 0; index < values.size(); index++) {
            if (Objects.equals(values.get(index), target)) {
                return SearchResult.found(index);
            }
        }

        return SearchResult.notFound();
    }
}
