interface SearchAlgorithm<T extends Comparable<? super T>> {
    SearchResult search(SearchRequest<T> request);
}
