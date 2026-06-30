class BinarySearchService<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    @Override
    public SearchResult search(SearchRequest<T> request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        T[] sortedValues = request.values();
        T target = request.target();

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T midValue = sortedValues[mid];

            if (midValue == null) {
                throw new IllegalArgumentException("sortedValues must not contain null elements");
            }

            int comparison = midValue.compareTo(target);
            if (comparison == 0) {
                return SearchResult.found(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }
}
