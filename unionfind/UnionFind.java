package unionfind;

import java.util.Arrays;
import java.util.Objects;

public final class UnionFind {
    private final int[] parent;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        componentCount = size;
        Arrays.fill(parent, -1);
    }

    public int find(int element) {
        Objects.checkIndex(element, parent.length);

        int root = element;
        while (!isRoot(root)) {
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

        mergeBySize(rootFirst, rootSecond);
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

    private boolean isRoot(int element) {
        return parent[element] < 0;
    }

    private int componentSize(int root) {
        return -parent[root];
    }

    private void mergeBySize(int rootFirst, int rootSecond) {
        if (componentSize(rootFirst) < componentSize(rootSecond)) {
            mergeRoots(rootFirst, rootSecond);
        } else {
            mergeRoots(rootSecond, rootFirst);
        }
    }

    private void mergeRoots(int childRoot, int parentRoot) {
        parent[parentRoot] += parent[childRoot];
        parent[childRoot] = parentRoot;
    }
}
