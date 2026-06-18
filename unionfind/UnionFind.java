package unionfind;

public final class UnionFind {
    private final int[] parent;
    private final int[] componentSize;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        componentSize = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            componentSize[i] = 1;
        }
    }

    public int find(int element) {
        validateElement(element);

        while (element != parent[element]) {
            parent[element] = parent[parent[element]];
            element = parent[element];
        }

        return element;
    }

    public boolean union(int first, int second) {
        int rootFirst = find(first);
        int rootSecond = find(second);

        if (rootFirst == rootSecond) {
            return false;
        }

        if (componentSize[rootFirst] < componentSize[rootSecond]) {
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

    public int size() {
        return parent.length;
    }

    private void linkRoots(int childRoot, int parentRoot) {
        parent[childRoot] = parentRoot;
        componentSize[parentRoot] += componentSize[childRoot];
        componentSize[childRoot] = 0;
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("element out of bounds: " + element);
        }
    }
}
