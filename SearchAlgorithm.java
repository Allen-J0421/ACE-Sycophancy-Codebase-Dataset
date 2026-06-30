interface SearchAlgorithm<T> {
    SearchResult search(T[] sortedArray, T target);
}
