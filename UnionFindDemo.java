public final class UnionFindDemo {
    private UnionFindDemo() {
    }

    public static void main(String[] args) {
        DisjointSet unionFind = new UnionFind(5);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        System.out.println("Are 1 and 2 in the same set? " + unionFind.connected(1, 2));
    }
}
