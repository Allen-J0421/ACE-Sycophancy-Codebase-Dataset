public class UnionFind {
    private final int[] parent;
    private final int[] rank;

    public UnionFind(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative");
        }

        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public int find(int element) {
        validateElement(element);

        if (parent[element] != element) {
            parent[element] = find(parent[element]);
        }

        return parent[element];
    }

    public boolean union(int first, int second) {
        int rootFirst = find(first);
        int rootSecond = find(second);

        if (rootFirst == rootSecond) {
            return false;
        }

        if (rank[rootFirst] < rank[rootSecond]) {
            parent[rootFirst] = rootSecond;
        } else if (rank[rootFirst] > rank[rootSecond]) {
            parent[rootSecond] = rootFirst;
        } else {
            parent[rootSecond] = rootFirst;
            rank[rootFirst]++;
        }

        return true;
    }

    public boolean connected(int first, int second) {
        return find(first) == find(second);
    }

    public int size() {
        return parent.length;
    }

    private void validateElement(int element) {
        if (element < 0 || element >= parent.length) {
            throw new IndexOutOfBoundsException("element out of bounds: " + element);
        }
    }

    public static void main(String[] args) {
        UnionFind unionFind = new UnionFind(5);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        boolean inSameSet = unionFind.connected(1, 2);
        System.out.println("Are 1 and 2 in the same set? " + inSameSet);
    }
}
