public class DisjointSetStructure {
    private int[] parent;
    private int[] rank;

    public DisjointSetStructure(int size) {
        parent = new int[size];
        rank = new int[size];
        initialize();
    }

    public int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    public void union(int irep, int jrep) {
        if (irep == jrep) {
            return;
        }
        performUnion(irep, jrep);
    }

    public void reset() {
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < parent.length; i++) {
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
}
