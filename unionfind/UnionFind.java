package unionfind;

public final class UnionFind {
    private final int[] parent;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = -1;
        }
    }

    public int find(int element) {
        validateElement(element);

        int root = element;
        while (parent[root] >= 0) {
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

        if (parent[rootFirst] > parent[rootSecond]) {
            linkRoots(rootFirst, rootSecond);
        } else {
            linkRoots(rootSecond, rootFirst);
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

    public int elementCount() {
        return parent.length;
    }

    private void linkRoots(int childRoot, int parentRoot) {
        parent[parentRoot] += parent[childRoot];
        parent[childRoot] = parentRoot;
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("element out of bounds: " + element);
        }
    }
}
