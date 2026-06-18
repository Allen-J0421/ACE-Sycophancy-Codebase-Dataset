package disjointset;

public final class ArrayDisjointSet implements DisjointSet {
    private static final String NEGATIVE_SIZE_MESSAGE = "size must be non-negative";

    private final int[] parent;
    private final int[] componentSize;
    private int componentCount;

    public ArrayDisjointSet(int size) {
        validateSize(size);
        parent = new int[size];
        componentSize = new int[size];
        componentCount = size;
        initializeSingletonSets();
    }

    @Override
    public int find(int element) {
        validateIndex(element);
        return findRoot(element);
    }

    @Override
    public boolean union(int first, int second) {
        int firstRoot = find(first);
        int secondRoot = find(second);
        if (firstRoot == secondRoot) {
            return false;
        }

        mergeDistinctRoots(largerRoot(firstRoot, secondRoot), smallerRoot(firstRoot, secondRoot));
        componentCount--;
        return true;
    }

    @Override
    public boolean connected(int first, int second) {
        return find(first) == find(second);
    }

    @Override
    public int componentSize(int element) {
        return componentSize[find(element)];
    }

    @Override
    public int components() {
        return componentCount;
    }

    @Override
    public int size() {
        return parent.length;
    }

    private void initializeSingletonSets() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            componentSize[i] = 1;
        }
    }

    private int findRoot(int element) {
        int root = rootOf(element);
        compressPathToRoot(element, root);
        return root;
    }

    private int rootOf(int element) {
        int root = element;
        while (parent[root] != root) {
            root = parent[root];
        }
        return root;
    }

    private void compressPathToRoot(int element, int root) {
        int current = element;
        while (current != root) {
            int next = parent[current];
            parent[current] = root;
            current = next;
        }
    }

    private int largerRoot(int firstRoot, int secondRoot) {
        if (componentSize[firstRoot] >= componentSize[secondRoot]) {
            return firstRoot;
        }
        return secondRoot;
    }

    private int smallerRoot(int firstRoot, int secondRoot) {
        if (componentSize[firstRoot] < componentSize[secondRoot]) {
            return firstRoot;
        }
        return secondRoot;
    }

    private void mergeDistinctRoots(int parentRoot, int childRoot) {
        attachRoot(childRoot, parentRoot);
    }

    private void attachRoot(int childRoot, int parentRoot) {
        parent[childRoot] = parentRoot;
        componentSize[parentRoot] += componentSize[childRoot];
    }

    private static void validateSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException(NEGATIVE_SIZE_MESSAGE);
        }
    }

    private void validateIndex(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException(outOfBoundsMessage(element));
        }
    }

    private String outOfBoundsMessage(int element) {
        return "index " + element + " is out of bounds for size " + parent.length;
    }
}
