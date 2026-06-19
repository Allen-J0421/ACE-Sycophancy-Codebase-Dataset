package lru;

/**
 * Least-recently-used eviction: both reads and writes move a key to the
 * most-protected position, so the key untouched for longest is evicted first.
 * O(1) per operation.
 */
public final class LruEvictionPolicy<K> extends OrderedEvictionPolicy<K> {

    @Override
    public void recordAccess(K key) {
        moveToTail(key); // a read or update marks the key most-recently-used
    }
}
