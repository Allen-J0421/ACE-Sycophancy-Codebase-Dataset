import java.util.Arrays;

public final class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFind(int size) {
        validateSize(size);
        parent = new int[size];
        rank = new int[size];
        components = size;

        initializeParents();
    }

    private static void validateSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
    }

    private void initializeParents() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
    }

    public int find(int element) {
        validateElement(element);
        return rootOf(element);
    }

    public boolean union(int first, int second) {
        int firstRoot = find(first);
        int secondRoot = find(second);

        if (firstRoot == secondRoot) {
            return false;
        }

        attachRoots(firstRoot, secondRoot);
        components--;
        return true;
    }

    public boolean connected(int first, int second) {
        return find(first) == find(second);
    }

    public int size() {
        return parent.length;
    }

    public int components() {
        return components;
    }

    @Override
    public String toString() {
        return "UnionFind{parent=" + Arrays.toString(parent)
                + ", rank=" + Arrays.toString(rank)
                + ", components=" + components
                + '}';
    }

    private int rootOf(int element) {
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

    private void attachRoots(int firstRoot, int secondRoot) {
        if (rank[firstRoot] < rank[secondRoot]) {
            parent[firstRoot] = secondRoot;
        } else if (rank[firstRoot] > rank[secondRoot]) {
            parent[secondRoot] = firstRoot;
        } else {
            parent[secondRoot] = firstRoot;
            rank[firstRoot]++;
        }
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException(invalidElementMessage(element));
        }
    }

    private String invalidElementMessage(int element) {
        if (parent.length == 0) {
            return "element is out of bounds for an empty UnionFind: " + element;
        }

        return "element must be between 0 and " + (parent.length - 1) + ": " + element;
    }
}
