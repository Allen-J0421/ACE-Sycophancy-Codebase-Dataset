package unionfind;

import java.util.Arrays;

public class UnionFind {
    private final int[] parent;
    private final int[] size;
    private int componentCount;

    public UnionFind(int n) {
        if (n < 0) throw new IllegalArgumentException("Capacity must be non-negative, got " + n);
        parent = new int[n];
        size = new int[n];
        reset();
    }

    public int find(int i) {
        validate(i);
        while (parent[i] != i) {
            parent[i] = parent[parent[i]]; // path halving
            i = parent[i];
        }
        return i;
    }

    public boolean union(int i, int j) {
        int ri = find(i);
        int rj = find(j);
        if (ri == rj) return false;

        if (size[ri] < size[rj]) {
            parent[ri] = rj;
            size[rj] += size[ri];
        } else {
            parent[rj] = ri;
            size[ri] += size[rj];
        }
        componentCount--;
        return true;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int componentSize(int i) {
        return size[find(i)];
    }

    public int[] componentMembers(int i) {
        int root = find(i);
        int[] temp = new int[parent.length];
        int count = 0;
        for (int k = 0; k < parent.length; k++) {
            if (find(k) == root) temp[count++] = k;
        }
        return Arrays.copyOf(temp, count);
    }

    public void reset() {
        componentCount = parent.length;
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int componentCount() {
        return componentCount;
    }

    public int capacity() {
        return parent.length;
    }

    @Override
    public String toString() {
        return "UnionFind[capacity=" + parent.length + ", components=" + componentCount + "]";
    }

    private void validate(int i) {
        if (i < 0 || i >= parent.length) {
            throw new IllegalArgumentException("Index " + i + " out of bounds for capacity " + parent.length);
        }
    }
}
