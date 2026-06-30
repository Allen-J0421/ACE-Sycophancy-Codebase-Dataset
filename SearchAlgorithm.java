interface SearchAlgorithm<T extends Comparable<? super T>> {
    int search(T[] values, T target);
}
