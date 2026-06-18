public class UnionFindDemo {
    public static void main(String[] args) {
        UnionFind unionFind = new UnionFind(5);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        boolean inSameSet = unionFind.connected(1, 2);
        System.out.println("Are 1 and 2 in the same set? " + inSameSet);
        System.out.println("Component count: " + unionFind.componentCount());
    }
}
