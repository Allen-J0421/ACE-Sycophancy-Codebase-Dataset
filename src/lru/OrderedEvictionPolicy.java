package lru;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Base for eviction policies that keep keys in a single linear order and always
 * evict from the head. Insertion, removal, and candidate selection are shared
 * here; subclasses decide only how — if at all — an access repositions a key by
 * implementing {@link #recordAccess}.
 *
 * <p>Backed by a {@link LinkedHashSet}; every operation is O(1).
 */
public abstract class OrderedEvictionPolicy<K> implements EvictionPolicy<K> {

    // Iteration runs from the eviction candidate (head) to the most-protected (tail).
    private final LinkedHashSet<K> order = new LinkedHashSet<>();

    @Override
    public void recordInsertion(K key) {
        order.add(key); // new keys enter at the tail, last in line to be evicted
    }

    @Override
    public void recordRemoval(K key) {
        order.remove(key);
    }

    @Override
    public K selectEvictionCandidate() {
        Iterator<K> it = order.iterator();
        return it.hasNext() ? it.next() : null; // head = next to evict
    }

    /** Moves an existing key to the tail, the most-protected position. */
    protected final void moveToTail(K key) {
        order.remove(key);
        order.add(key);
    }
}
