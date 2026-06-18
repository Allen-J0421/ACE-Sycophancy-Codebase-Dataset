public class UnionFind {
    private int[] parent;
    private int[] rank;
    private int size;

    public UnionFind(int size) {
        validateSize(size);
        this.size = size;
        parent = new int[size];
        rank = new int[size];
        initializeStructure();
    }

    public int find(int i) {
        validateIndex(i);
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    public void union(int i, int j) {
        validateIndex(i);
        validateIndex(j);

        int irep = find(i);
        int jrep = find(j);

        if (irep == jrep) {
            return;
        }

        performUnion(irep, jrep);
    }

    public boolean isConnected(int i, int j) {
        validateIndex(i);
        validateIndex(j);
        return find(i) == find(j);
    }

    public int getSize() {
        return size;
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
