public class UnionFind {
    private int[] parent;
    private int[] rank;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    public void union(int i, int j) {
        int irep = find(i);
        int jrep = find(j);

        if (irep == jrep) {
            return;
        }

        if (rank[irep] < rank[jrep]) {
            parent[irep] = jrep;
        } else if (rank[irep] > rank[jrep]) {
            parent[jrep] = irep;
        } else {
            parent[jrep] = irep;
            rank[irep]++;
        }
    }

    public boolean isConnected(int i, int j) {
        return find(i) == find(j);
    }

    public static void main(String[] args) {
        testBasicUnion();
        testConnectivity();
        testMultipleUnions();
    }

    private static void testBasicUnion() {
        UnionFind uf = new UnionFind(5);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(1, 2) : "1 and 2 should be connected";
        assert !uf.isConnected(2, 4) : "2 and 4 should not be connected";
        System.out.println("✓ testBasicUnion passed");
    }

    private static void testConnectivity() {
        UnionFind uf = new UnionFind(6);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(0, 2) : "0 and 2 should be transitively connected";
        assert !uf.isConnected(2, 3) : "2 and 3 should not be connected";
        System.out.println("✓ testConnectivity passed");
    }

    private static void testMultipleUnions() {
        UnionFind uf = new UnionFind(10);
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(5, 6);
        uf.union(3, 4);
        assert uf.isConnected(1, 6) : "1 and 6 should be connected after union chain";
        System.out.println("✓ testMultipleUnions passed");
    }
}
