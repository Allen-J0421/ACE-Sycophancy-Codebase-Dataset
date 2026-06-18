public class CachingDecorator implements GraphTraversal {
    private final GraphTraversal delegate;
    private final CacheStrategy<String, TraversalResult> cache;

    public CachingDecorator(GraphTraversal delegate, CacheStrategy<String, TraversalResult> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public TraversalResult traverse(Graph graph) {
        String cacheKey = generateKey(graph);
        TraversalResult cached = cache.get(cacheKey);

        if (cached != null) {
            Logger.debug("Cache hit: " + cacheKey);
            return cached;
        }

        Logger.debug("Cache miss: " + cacheKey + ", computing...");
        TraversalResult result = delegate.traverse(graph);
        cache.put(cacheKey, result);
        return result;
    }

    private String generateKey(Graph graph) {
        return delegate.getClass().getSimpleName() + "_" + System.identityHashCode(graph);
    }

    public int getCacheSize() {
        return cache.getSize();
    }

    public void clearCache() {
        cache.clear();
    }
}
