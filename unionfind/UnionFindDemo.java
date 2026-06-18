package unionfind;

public class UnionFindDemo {
    public static void main(String[] args) {
        UnionFind uf = new UnionFind(6);

        System.out.println("Edges: 0-1, 1-2, 3-4");
        boolean merged;
        merged = uf.union(0, 1); System.out.println("union(0,1): merged=" + merged);
        merged = uf.union(1, 2); System.out.println("union(1,2): merged=" + merged);
        merged = uf.union(3, 4); System.out.println("union(3,4): merged=" + merged);
        merged = uf.union(0, 2); System.out.println("union(0,2): merged=" + merged + " (already connected)");

        System.out.println();
        System.out.println("connected(0,2): " + uf.connected(0, 2));
        System.out.println("connected(0,3): " + uf.connected(0, 3));
        System.out.println("componentSize(0): " + uf.componentSize(0));
        System.out.println("componentSize(3): " + uf.componentSize(3));
        System.out.println("componentSize(5): " + uf.componentSize(5) + " (isolated)");
        System.out.println("components: " + uf.getComponentCount());
    }
}
