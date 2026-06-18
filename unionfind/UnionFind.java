package unionfind;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int componentCount;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]]; // path halving
            i = parent[i];
        }
        return i;
    }

    public void union(int i, int j) {
        int ri = find(i);
        int rj = find(j);
        if (ri == rj) return;

        if (rank[ri] < rank[rj]) {
            parent[ri] = rj;
        } else if (rank[ri] > rank[rj]) {
            parent[rj] = ri;
        } else {
            parent[rj] = ri;
            rank[ri]++;
        }
        componentCount--;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int getComponentCount() {
        return componentCount;
    }
}
