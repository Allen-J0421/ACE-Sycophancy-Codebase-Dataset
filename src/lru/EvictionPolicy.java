package lru;

/**
 * Strategy that decides which key a capacity-bound {@link Cache} evicts.
 *
 * <p>The cache reports lifecycle events as they happen — a key is inserted,
 * accessed, or removed — and asks the policy to nominate a victim whenever
 * capacity is exceeded. Implementations maintain whatever bookkeeping their
 * algorithm requires (recency, insertion order, frequency, …) and need not know
 * anything about values or the cache's storage.
 *
 * @param <K> the key type tracked for eviction
 */
public interface EvictionPolicy<K> {

    /** Records that a brand-new key was added to the cache. */
    void recordInsertion(K key);

    /** Records that an existing key was read or updated. */
    void recordAccess(K key);

    /** Records that a key has left the cache (via eviction or otherwise). */
    void recordRemoval(K key);

    /**
     * Returns the key that should be evicted next, or {@code null} if the policy
     * is currently tracking no keys.
     */
    K selectEvictionCandidate();
}
