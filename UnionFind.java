public class UnionFind implements DisjointSet {
    private int[] parent;
    private int[] rank;
    private int size;
    private long findCount;
    private long unionCount;

    public UnionFind(int size) {
        validateSize(size);
        this.size = size;
        parent = new int[size];
        rank = new int[size];
        findCount = 0;
        unionCount = 0;
        initializeStructure();
    }

    @Override
    public int find(int i) {
        validateIndex(i);
        findCount++;
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    @Override
    public void union(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        unionCount++;

        int irep = find(i);
        int jrep = find(j);

        if (irep == jrep) {
            return;
        }

        performUnion(irep, jrep);
    }

    @Override
    public boolean isConnected(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        return find(i) == find(j);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Statistics getStatistics() {
        return new Statistics(findCount, unionCount);
    }

    public void reset() {
        findCount = 0;
        unionCount = 0;
        initializeStructure();
    }

    private void initializeStructure() {
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    private void performUnion(int irep, int jrep) {
        if (rank[irep] < rank[jrep]) {
            parent[irep] = jrep;
        } else if (rank[irep] > rank[jrep]) {
            parent[jrep] = irep;
        } else {
            parent[jrep] = irep;
            rank[irep]++;
        }
    }

    private void validateSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("UnionFind size must be positive");
        }
    }

    private void validateIndex(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds [0, " + size + ")");
        }
    }
}
