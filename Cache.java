import java.util.Optional;

public interface Cache<K, V> {
    Optional<V> get(K key);

    boolean containsKey(K key);

    void put(K key, V value);

    Optional<V> remove(K key);

    int size();

    int capacity();

    void clear();

    default V getOrDefault(K key, V defaultValue) {
        return get(key).orElse(defaultValue);
    }

    default boolean isEmpty() {
        return size() == 0;
    }
}
