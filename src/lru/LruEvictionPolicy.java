package lru;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Least-recently-used eviction: both reads and writes mark a key as the most
 * recently used, so the key untouched for longest is evicted first.
 *
 * <p>Backed by a {@link LinkedHashSet} whose iteration order runs from least- to
 * most-recently-used; every operation is O(1).
 */
public final class LruEvictionPolicy<K> implements EvictionPolicy<K> {

    private final LinkedHashSet<K> recency = new LinkedHashSet<>();

    @Override
    public void recordInsertion(K key) {
        recency.add(key); // newest entries start at the most-recently-used tail
    }

    @Override
    public void recordAccess(K key) {
        recency.remove(key);
        recency.add(key); // re-append to move the key to the most-recently-used tail
    }

    @Override
    public void recordRemoval(K key) {
        recency.remove(key);
    }

    @Override
    public K selectEvictionCandidate() {
        Iterator<K> it = recency.iterator();
        return it.hasNext() ? it.next() : null; // head = least-recently-used
    }
}
