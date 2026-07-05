import java.util.Arrays;

interface DisjointSet {
    int find(int i);
    void union(int i, int j);
    boolean connected(int i, int j);
    int size();
    int count();
    void reset();
}

public class UnionFind implements DisjointSet {
    private final int[] parent;
    private final int[] rank;
    private int count;

    public UnionFind(int size) {

        parent = new int[size];
        rank = new int[size];
        count = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    private static void validate(int i, int size) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("Index " + i + " out of bounds for size " + size);
        }
    }

    public int find(int i) {
        validate(i, parent.length);

        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }

        return parent[i];
    }

    public void union(int i, int j) {
        validate(i, parent.length);
        validate(j, parent.length);

        int irep = find(i);

        int jrep = find(j);

        if (irep == jrep) return;

        count--;

        if (rank[irep] < rank[jrep]) {
            parent[irep] = jrep;
        } else if (rank[irep] > rank[jrep]) {
            parent[jrep] = irep;
        } else {
            parent[jrep] = irep;
            rank[irep]++;
        }
    }

    public int size() {
        return parent.length;
    }

    public int count() {
        return count;
    }

    public boolean connected(int i, int j) {
        validate(i, parent.length);
        validate(j, parent.length);
        return find(i) == find(j);
    }

    public void reset() {
        count = parent.length;
        Arrays.fill(rank, 0);
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }

    public static void main(String[] args) {
        int size = 5;
        DisjointSet uf = new UnionFind(size);
        uf.union(1, 2);
        uf.union(3, 4);
        System.out.println("Are 1 and 2 in the same set? " + uf.connected(1, 2));
    }
}
