import java.util.HashMap;
import java.util.Map;

public class TraversalCache {
    private final Map<String, TraversalResult> cache = new HashMap<>();
    private final Map<String, Long> timestamps = new HashMap<>();
    private final long ttlMillis;

    public TraversalCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public TraversalCache() {
        this(5 * 60 * 1000);
    }

    public String generateKey(Graph graph, GraphTraversal strategy) {
        return strategy.getClass().getSimpleName() + "_" + System.identityHashCode(graph);
    }

    public TraversalResult get(String key) {
        Long timestamp = timestamps.get(key);
        if (timestamp == null) {
            return null;
        }

        if (System.currentTimeMillis() - timestamp > ttlMillis) {
            cache.remove(key);
            timestamps.remove(key);
            return null;
        }

        return cache.get(key);
    }

    public void put(String key, TraversalResult result) {
        cache.put(key, result);
        timestamps.put(key, System.currentTimeMillis());
    }

    public void invalidate(String key) {
        cache.remove(key);
        timestamps.remove(key);
    }

    public void clear() {
        cache.clear();
        timestamps.clear();
    }

    public int getSize() {
        return cache.size();
    }

    public boolean isExpired(String key) {
        Long timestamp = timestamps.get(key);
        return timestamp != null && System.currentTimeMillis() - timestamp > ttlMillis;
    }
}
