final class DisjointSet {
    private final int[] parent;
    private final int[] rank;

    DisjointSet(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Disjoint set size must be non-negative.");
        }

        this.parent = new int[size];
        this.rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    int find(int node) {
        if (parent[node] != node) {
            parent[node] = find(parent[node]);
        }
        return parent[node];
    }

    boolean union(int first, int second) {
        int rootFirst = find(first);
        int rootSecond = find(second);
        if (rootFirst == rootSecond) {
            return false;
        }

        if (rank[rootFirst] < rank[rootSecond]) {
            parent[rootFirst] = rootSecond;
        } else if (rank[rootFirst] > rank[rootSecond]) {
            parent[rootSecond] = rootFirst;
        } else {
            parent[rootSecond] = rootFirst;
            rank[rootFirst]++;
        }
        return true;
    }
}
