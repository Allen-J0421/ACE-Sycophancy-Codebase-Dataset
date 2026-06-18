public class UnionFind {
    private final int[] parent;
    private final int[] subtreeSize;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        subtreeSize = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            subtreeSize[i] = 1;
        }
    }

    public int find(int element) {
        validateElement(element);

        int root = findRoot(element);
        compressPath(element, root);
        return root;
    }

    public boolean union(int first, int second) {
        int rootFirst = find(first);
        int rootSecond = find(second);

        if (rootFirst == rootSecond) {
            return false;
        }

        if (subtreeSize[rootFirst] < subtreeSize[rootSecond]) {
            attachRoot(rootFirst, rootSecond);
        } else {
            attachRoot(rootSecond, rootFirst);
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

    private int findRoot(int element) {
        int root = element;
        while (root != parent[root]) {
            root = parent[root];
        }
        return root;
    }

    private void compressPath(int element, int root) {
        while (element != root) {
            int next = parent[element];
            parent[element] = root;
            element = next;
        }
    }

    private void attachRoot(int childRoot, int parentRoot) {
        parent[childRoot] = parentRoot;
        subtreeSize[parentRoot] += subtreeSize[childRoot];
        subtreeSize[childRoot] = 0;
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("element out of bounds: " + element);
        }
    }
}
