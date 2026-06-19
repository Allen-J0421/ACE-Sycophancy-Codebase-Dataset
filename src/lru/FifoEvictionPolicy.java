package lru;

/**
 * First-in, first-out eviction: the oldest-inserted key is evicted first.
 * Accesses — and updates of an already-present key — do not change eviction
 * order. O(1) per operation.
 */
public final class FifoEvictionPolicy<K> extends OrderedEvictionPolicy<K> {

    @Override
    public void recordAccess(K key) {
        // FIFO ignores access; eviction depends only on insertion order.
    }
}
