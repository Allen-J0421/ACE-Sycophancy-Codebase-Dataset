public final class UnionFind implements DisjointSet {
    private final int[] parent;
    private final int[] rank;
    private final int[] componentSize;
    private int componentCount;

    public UnionFind(int size) {
        validateSize(size);
        parent = new int[size];
        rank = new int[size];
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

        mergeDistinctRoots(firstRoot, secondRoot);
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
        int current = element;
        int root = current;
        while (parent[root] != root) {
            root = parent[root];
        }

        while (current != root) {
            int next = parent[current];
            parent[current] = root;
            current = next;
        }

        return root;
    }

    private void mergeDistinctRoots(int firstRoot, int secondRoot) {
        if (rank[firstRoot] < rank[secondRoot]) {
            attachRoot(firstRoot, secondRoot);
            return;
        }

        if (rank[firstRoot] > rank[secondRoot]) {
            attachRoot(secondRoot, firstRoot);
            return;
        }

        attachRoot(secondRoot, firstRoot);
        rank[firstRoot]++;
    }

    private void attachRoot(int childRoot, int parentRoot) {
        parent[childRoot] = parentRoot;
        componentSize[parentRoot] += componentSize[childRoot];
    }

    private static void validateSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
    }

    private void validateIndex(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException(
                "index " + element + " is out of bounds for size " + parent.length
            );
        }
    }
}
