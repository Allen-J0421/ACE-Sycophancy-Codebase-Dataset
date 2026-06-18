public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private final int[] componentSize;
    private int componentCount;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }
        parent = new int[size];
        rank = new int[size];
        componentSize = new int[size];
        componentCount = size;
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            componentSize[i] = 1;
        }
    }

    public int find(int i) {
        validateIndex(i);
        int current = i;
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

    public boolean union(int i, int j) {
        int firstRoot = find(i);
        int secondRoot = find(j);
        if (firstRoot == secondRoot) {
            return false;
        }

        linkRoots(firstRoot, secondRoot);
        componentCount--;
        return true;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }

    public int componentSize(int i) {
        return componentSize[find(i)];
    }

    public int components() {
        return componentCount;
    }

    public int size() {
        return parent.length;
    }

    private void linkRoots(int firstRoot, int secondRoot) {
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

    private void validateIndex(int i) {
        if (i < 0 || i >= parent.length) {
            throw new IndexOutOfBoundsException(
                "index " + i + " is out of bounds for size " + parent.length
            );
        }
    }

    public static void main(String[] args) {
        int size = 5;
        UnionFind uf = new UnionFind(size);
        uf.union(1, 2);
        uf.union(3, 4);
        boolean inSameSet = uf.connected(1, 2);
        System.out.println("Are 1 and 2 in the same set? " + inSameSet);
        System.out.println("Component count: " + uf.components());
        System.out.println("Component size containing 1: " + uf.componentSize(1));
    }
}
