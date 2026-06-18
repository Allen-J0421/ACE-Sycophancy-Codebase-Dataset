import java.util.Arrays;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        rank = new int[size];
        components = size;

        for (int i = 0; i < size; i++) {
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
        int firstRoot = find(first);
        int secondRoot = find(second);

        return firstRoot == secondRoot;
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
            throw new IndexOutOfBoundsException(
                    "element must be between 0 and " + (parent.length - 1) + ": " + element
            );
        }
    }

    public static void main(String[] args) {
        UnionFind unionFind = new UnionFind(5);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        System.out.println("Are 1 and 2 in the same set? " + unionFind.connected(1, 2));
    }
}
