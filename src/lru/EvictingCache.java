package lru;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A fixed-capacity {@link Cache} whose eviction behaviour is supplied by a
 * pluggable {@link EvictionPolicy}.
 *
 * <p>The cache owns key/value storage and enforces the capacity bound; the
 * policy decides <em>which</em> key to drop when that bound is exceeded. Swapping
 * the policy (LRU, FIFO, …) changes eviction behaviour without touching this
 * class. Given an O(1) policy, all operations run in O(1).
 *
 * <p>This implementation is not thread-safe.
 */
public class EvictingCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> store;
    private final EvictionPolicy<K> policy;

    public EvictingCache(int capacity, EvictionPolicy<K> policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        this.policy = Objects.requireNonNull(policy, "policy");
        this.store = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return getOrDefault(key, null);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        if (!store.containsKey(key)) {
            return defaultValue;
        }
        policy.recordAccess(key);
        return store.get(key);
    }

    @Override
    public void put(K key, V value) {
        if (store.containsKey(key)) {
            store.put(key, value);
            policy.recordAccess(key);
            return;
        }

        store.put(key, value);
        policy.recordInsertion(key);

        if (store.size() > capacity) {
            K victim = policy.selectEvictionCandidate();
            if (victim != null) {
                store.remove(victim);
                policy.recordRemoval(victim);
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        return store.containsKey(key);
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
