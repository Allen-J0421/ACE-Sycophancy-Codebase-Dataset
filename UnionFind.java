public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        rank = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int element) {
        validateElement(element);

        int root = element;
        while (root != parent[root]) {
            root = parent[root];
        }

        while (element != root) {
            int next = parent[element];
            parent[element] = root;
            element = next;
        }

        return root;
    }

    public boolean union(int first, int second) {
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

        componentCount--;
        return true;
    }

    public boolean connected(int first, int second) {
        return find(first) == find(second);
    }

    public int componentCount() {
        return componentCount;
    }

    public int size() {
        return parent.length;
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("element out of bounds: " + element);
        }
    }
}
