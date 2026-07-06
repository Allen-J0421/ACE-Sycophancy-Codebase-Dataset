import java.util.Set;

interface HashMapOperations<K, V> extends Iterable<K> {
    void insertNode(K key, V value);
    V deleteNode(K key);
    V get(K key);
    int getSize();
    boolean isEmpty();
    void display();
    void clear();
    Set<K> keySet();
}
