import java.util.Arrays;

interface DisjointSet {
    int find(int i);
    void union(int i, int j);
}

public class UnionFind implements DisjointSet {
    private int[] parent;
    private int[] rank;

    public UnionFind(int size) {

        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
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

        if (irep == jrep) return;

        if (rank[irep] < rank[jrep]) {
            parent[irep] = jrep;
        } else if (rank[irep] > rank[jrep]) {
            parent[jrep] = irep;
        } else {
            parent[jrep] = irep;
            rank[irep]++;
        }
    }

    public static void main(String[] args) {
        int size = 5;
        DisjointSet uf = new UnionFind(size);
        uf.union(1, 2);
        uf.union(3, 4);
        boolean inSameSet = uf.find(1) == uf.find(2);
        System.out.println("Are 1 and 2 in the same set? " + inSameSet);
    }
}
