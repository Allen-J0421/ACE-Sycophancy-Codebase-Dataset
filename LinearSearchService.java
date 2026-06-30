class LinearSearchService<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    @Override
    public SearchResult search(SearchRequest<T> request) {
        T[] values = request.values();
        T target = request.target();

        for (int index = 0; index < values.length; index++) {
            T value = values[index];

            if (value.compareTo(target) == 0) {
                return SearchResult.found(index);
            }
        }

        return SearchResult.notFound();
    }
}
