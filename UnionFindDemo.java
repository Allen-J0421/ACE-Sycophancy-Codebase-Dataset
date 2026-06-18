public final class UnionFindDemo {
    private UnionFindDemo() {
    }

    public static void main(String[] args) {
        DisjointSet disjointSet = DisjointSet.create(5);
        disjointSet.union(1, 2);
        disjointSet.union(3, 4);

        System.out.println("Are 1 and 2 in the same set? " + disjointSet.connected(1, 2));
        System.out.println("Component count: " + disjointSet.components());
        System.out.println("Component size containing 1: " + disjointSet.componentSize(1));
    }
}
