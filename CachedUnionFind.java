import java.util.HashMap;
import java.util.Map;

public class CachedUnionFind implements UnionFindDelegate {
    private UnionFindDelegate delegate;
    private Map<Integer, Integer> rootCache;
    private boolean cacheEnabled;

    public CachedUnionFind(UnionFindDelegate delegate) {
        this.delegate = delegate;
        this.rootCache = new HashMap<>();
        this.cacheEnabled = true;
    }

    public int find(int i) {
        if (!cacheEnabled) {
            return delegate.find(i);
        }

        if (rootCache.containsKey(i)) {
            return rootCache.get(i);
        }

        int root = delegate.find(i);
        rootCache.put(i, root);
        return root;
    }

    public void union(int i, int j) {
        delegate.union(i, j);
        invalidateCache();
    }

    public boolean isConnected(int i, int j) {
        return find(i) == find(j);
    }

    public void invalidateCache() {
        rootCache.clear();
    }

    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
        if (!enabled) {
            rootCache.clear();
        }
    }

    public int getCacheSize() {
        return rootCache.size();
    }

    public void reset() {
        if (delegate instanceof UnionFind) {
            ((UnionFind) delegate).reset();
        }
        rootCache.clear();
    }

    public static CachedUnionFind wrap(UnionFind uf) {
        return new CachedUnionFind(uf);
    }

    public static CachedUnionFind wrap(UnionFindDelegate delegate) {
        return new CachedUnionFind(delegate);
    }
}
