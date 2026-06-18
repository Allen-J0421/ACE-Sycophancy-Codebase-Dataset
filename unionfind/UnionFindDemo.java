package unionfind;

public class UnionFindDemo {
    public static void main(String[] args) {
        UnionFind uf = new UnionFind(5);
        uf.union(1, 2);
        uf.union(3, 4);

        System.out.println("Are 1 and 2 in the same set? " + uf.connected(1, 2));
        System.out.println("Are 1 and 3 in the same set? " + uf.connected(1, 3));
        System.out.println("Component count: " + uf.getComponentCount());

        uf.union(2, 3);
        System.out.println("After union(2,3):");
        System.out.println("Are 1 and 4 in the same set? " + uf.connected(1, 4));
        System.out.println("Component count: " + uf.getComponentCount());
    }
}
