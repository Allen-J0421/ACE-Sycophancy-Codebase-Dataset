interface SearchTree<T extends Comparable<T>> {
    void insert(T key);
    boolean search(T key);
}
