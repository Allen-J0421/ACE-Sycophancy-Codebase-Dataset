public class CompositeUnionFind {
    private UnionFind base;
    private CachedUnionFind cached;
    private BatchUnionFind batched;

    public CompositeUnionFind(UnionFind base) {
        this.base = base;
        this.cached = new CachedUnionFind(base);
        this.batched = new BatchUnionFind(cached);
    }

    public UnionFind getBase() {
        return base;
    }

    public CachedUnionFind getCached() {
        return cached;
    }

    public BatchUnionFind getBatched() {
        return batched;
    }

    public int find(int i) {
        return batched.find(i);
    }

    public void union(int i, int j) {
        batched.union(i, j);
    }

    public boolean isConnected(int i, int j) {
        return batched.isConnected(i, j);
    }

    public void startBatch() {
        batched.startBatch();
    }

    public int flushBatch() {
        return batched.flushBatch();
    }

    public void invalidateCache() {
        cached.invalidateCache();
    }

    public void reset() {
        batched.reset();
    }

    public static CompositeUnionFind create(UnionFind base) {
        return new CompositeUnionFind(base);
    }
}
