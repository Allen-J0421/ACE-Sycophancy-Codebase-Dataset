package unionfind;

public class UnionFind {
    private final int[] parent;
    private final int[] size;
    private int componentCount;

    public UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        componentCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int i) {
        validate(i);
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

        if (size[ri] < size[rj]) {
            parent[ri] = rj;
            size[rj] += size[ri];
        } else {
            parent[rj] = ri;
            size[ri] += size[rj];
        }
        componentCount--;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int getComponentCount() {
        return componentCount;
    }

    public int capacity() {
        return parent.length;
    }

    private void validate(int i) {
        if (i < 0 || i >= parent.length) {
            throw new IllegalArgumentException("Index " + i + " out of bounds for capacity " + parent.length);
        }
    }
}
