public class UnionFind {
    private final int[] parent;
    private final int[] rank;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int i) {
        validateIndex(i);
        int root = i;
        while (parent[root] != root) {
            root = parent[root];
        }

        while (i != root) {
            int next = parent[i];
            parent[i] = root;
            i = next;
        }

        return root;
    }

    public void union(int i, int j) {
        int firstRoot = find(i);
        int secondRoot = find(j);
        if (firstRoot == secondRoot) {
            return;
        }

        if (rank[firstRoot] < rank[secondRoot]) {
            parent[firstRoot] = secondRoot;
            return;
        }

        if (rank[firstRoot] > rank[secondRoot]) {
            parent[secondRoot] = firstRoot;
            return;
        }

        parent[secondRoot] = firstRoot;
        rank[firstRoot]++;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int size() {
        return parent.length;
    }

    private void validateIndex(int i) {
        if (i < 0 || i >= parent.length) {
            throw new IndexOutOfBoundsException(
                "index " + i + " is out of bounds for size " + parent.length
            );
        }
    }

    public static void main(String[] args) {
        int size = 5;
        UnionFind uf = new UnionFind(size);
        uf.union(1, 2);
        uf.union(3, 4);
        boolean inSameSet = uf.connected(1, 2);
        System.out.println("Are 1 and 2 in the same set? " + inSameSet);
    }
}
