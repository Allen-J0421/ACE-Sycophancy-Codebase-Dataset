package lru;

/**
 * A fixed-capacity key/value store with a defined eviction policy.
 */
public interface Cache<K, V> {

    /** Returns the value mapped to {@code key}, or {@code null} if absent. */
    V get(K key);

    /** Returns the value mapped to {@code key}, or {@code defaultValue} if absent. */
    V getOrDefault(K key, V defaultValue);

    /** Inserts or updates the mapping for {@code key}. */
    void put(K key, V value);

    /** Returns {@code true} if a mapping for {@code key} exists. */
    boolean containsKey(K key);

    /** Returns the number of entries currently held. */
    int size();

    /** Returns the maximum number of entries this cache may hold. */
    int capacity();
}
