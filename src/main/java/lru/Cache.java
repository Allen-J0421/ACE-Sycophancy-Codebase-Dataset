package lru;

/**
 * Minimal cache contract shared by cache implementations.
 */
public interface Cache<K, V> {
    int capacity();

    V get(K key);

    default V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    void put(K key, V value);

    V remove(K key);

    boolean containsKey(K key);

    void clear();

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
