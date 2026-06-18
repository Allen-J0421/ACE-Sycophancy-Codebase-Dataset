import java.util.Arrays;

public final class UnionFind {
    private final int[] parent;
    private final int[] componentSizes;
    private int components;

    public UnionFind(int size) {
        validateSize(size);
        parent = new int[size];
        componentSizes = new int[size];
        components = size;

        initializeComponents();
    }

    private static void validateSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
    }

    private void initializeComponents() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            componentSizes[i] = 1;
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

    public int componentSize(int element) {
        return componentSizes[find(element)];
    }

    @Override
    public String toString() {
        return "UnionFind{parent=" + Arrays.toString(parent)
                + ", componentSizes=" + Arrays.toString(componentSizes)
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
        if (componentSizes[firstRoot] < componentSizes[secondRoot]) {
            int smallerRoot = firstRoot;
            firstRoot = secondRoot;
            secondRoot = smallerRoot;
        }

        parent[secondRoot] = firstRoot;
        componentSizes[firstRoot] += componentSizes[secondRoot];
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
