package lru;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * First-in, first-out eviction: the oldest-inserted key is evicted first.
 * Reads do not change eviction order, and updating an existing key does not
 * reset its position.
 *
 * <p>Backed by a {@link LinkedHashSet} whose iteration order is insertion order;
 * every operation is O(1).
 */
public final class FifoEvictionPolicy<K> implements EvictionPolicy<K> {

    private final LinkedHashSet<K> insertionOrder = new LinkedHashSet<>();

    @Override
    public void recordInsertion(K key) {
        insertionOrder.add(key);
    }

    @Override
    public void recordAccess(K key) {
        // FIFO is indifferent to access; eviction depends only on insertion order.
    }

    @Override
    public void recordRemoval(K key) {
        insertionOrder.remove(key);
    }

    @Override
    public K selectEvictionCandidate() {
        Iterator<K> it = insertionOrder.iterator();
        return it.hasNext() ? it.next() : null; // head = oldest insertion
    }
}
