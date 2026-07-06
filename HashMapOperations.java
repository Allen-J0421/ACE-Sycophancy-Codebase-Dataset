interface HashMapOperations<K, V> {
    void insertNode(K key, V value);
    V deleteNode(K key);
    V get(K key);
    int getSize();
    boolean isEmpty();
    void display();
    void clear();
}
