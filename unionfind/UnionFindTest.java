package unionfind;

public class UnionFindTest {
    public static void main(String[] args) {
        TestSupport t = new TestSupport();
        t.runSection("Initial state",        () -> testInitialState(t));
        t.runSection("union() return value", () -> testUnionReturnValue(t));
        t.runSection("Connectivity",         () -> testConnectivity(t));
        t.runSection("Component count",      () -> testComponentCount(t));
        t.runSection("Component size",       () -> testComponentSize(t));
        t.runSection("Reset",                () -> testReset(t));
        t.runSection("Component members",    () -> testComponentMembers(t));
        t.runSection("Large structure",      () -> testLargeStructure(t));
        t.runSection("Boundary conditions",  () -> testBoundaryConditions(t));
        t.printSummary();
        if (!t.allPassed()) System.exit(1);
    }

    private static void testInitialState(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertEquals("component count starts at n", 5, uf.componentCount());
        t.assertEquals("capacity", 5, uf.capacity());
        t.assertFalse("distinct elements not connected initially", uf.connected(0, 1));
        t.assertTrue("element is connected to itself", uf.connected(0, 0));
        t.assertEquals("initial component size is 1", 1, uf.componentSize(0));
        t.assertTrue("toString includes capacity", uf.toString().contains("capacity=5"));
        t.assertTrue("toString includes component count", uf.toString().contains("components=5"));
    }

    private static void testUnionReturnValue(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertTrue("union of disjoint elements returns true", uf.union(0, 1));
        t.assertFalse("union of already-connected elements returns false", uf.union(0, 1));
        uf.union(1, 2);
        t.assertFalse("union via transitive connection returns false", uf.union(0, 2));
    }

    private static void testConnectivity(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        uf.union(0, 1);
        t.assertTrue("connected after direct union", uf.connected(0, 1));

        uf.union(1, 2);
        t.assertTrue("connected transitively through chain", uf.connected(0, 2));

        t.assertFalse("element in separate component not connected", uf.connected(0, 3));

        uf.union(3, 4);
        uf.union(2, 3);
        t.assertTrue("all elements connected after full merge", uf.connected(0, 4));
    }

    private static void testComponentCount(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertEquals("starts with n components", 5, uf.componentCount());

        uf.union(0, 1);
        t.assertEquals("one union reduces count by one", 4, uf.componentCount());

        uf.union(1, 2);
        t.assertEquals("second union reduces count again", 3, uf.componentCount());

        uf.union(0, 2);
        t.assertEquals("redundant union leaves count unchanged", 3, uf.componentCount());

        uf.union(3, 4);
        uf.union(2, 3);
        t.assertEquals("all merged into one component", 1, uf.componentCount());
    }

    private static void testComponentSize(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertEquals("singleton component has size 1", 1, uf.componentSize(0));

        uf.union(0, 1);
        t.assertEquals("element 0 reports component size 2", 2, uf.componentSize(0));
        t.assertEquals("element 1 reports component size 2", 2, uf.componentSize(1));

        uf.union(1, 2);
        t.assertEquals("component size after merging 3", 3, uf.componentSize(2));

        uf.union(3, 4);
        t.assertEquals("separate component has its own size", 2, uf.componentSize(3));
    }

    private static void testReset(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        t.assertEquals("component count before reset", 2, uf.componentCount());

        uf.reset();
        t.assertEquals("component count restored after reset", 5, uf.componentCount());
        t.assertFalse("elements disconnected after reset", uf.connected(0, 1));
        t.assertEquals("component size restored to 1 after reset", 1, uf.componentSize(0));
        t.assertArrayEquals("componentMembers returns singleton after reset", new int[]{0}, uf.componentMembers(0));

        uf.union(0, 1);
        t.assertTrue("structure usable after reset", uf.connected(0, 1));
    }

    private static void testComponentMembers(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertArrayEquals("isolated element is its own member", new int[]{2}, uf.componentMembers(2));

        uf.union(0, 1);
        uf.union(1, 3);
        // componentMembers iterates k=0..n-1, so results are in ascending order
        t.assertArrayEquals("merged component contains exactly {0,1,3}",
                new int[]{0, 1, 3}, uf.componentMembers(0));
        t.assertArrayEquals("any member of the component returns the same result",
                new int[]{0, 1, 3}, uf.componentMembers(1));
        t.assertArrayEquals("isolated element excluded from merged component",
                new int[]{2}, uf.componentMembers(2));
    }

    private static void testLargeStructure(TestSupport t) {
        int n = 1000;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n - 1; i++) {
            uf.union(i, i + 1);
        }
        t.assertEquals("single component after chaining " + n + " elements", 1, uf.componentCount());
        t.assertTrue("first and last element connected", uf.connected(0, n - 1));
        t.assertEquals("component size equals n", n, uf.componentSize(0));
        t.assertEquals("componentMembers returns all n elements", n, uf.componentMembers(0).length);
    }

    private static void testBoundaryConditions(TestSupport t) {
        UnionFind single = new UnionFind(1);
        t.assertEquals("single-element structure has one component", 1, single.componentCount());
        t.assertTrue("single element is connected to itself", single.connected(0, 0));
        t.assertEquals("single element has component size 1", 1, single.componentSize(0));

        UnionFind empty = new UnionFind(0);
        t.assertEquals("zero-capacity structure has zero components", 0, empty.componentCount());

        UnionFind uf = new UnionFind(5);
        t.assertThrows("negative constructor arg throws", IllegalArgumentException.class,
                () -> new UnionFind(-1));
        t.assertThrows("find with negative index throws", IllegalArgumentException.class,
                () -> uf.find(-1));
        t.assertThrows("find with index equal to capacity throws", IllegalArgumentException.class,
                () -> uf.find(5));
        t.assertThrows("connected with invalid index throws", IllegalArgumentException.class,
                () -> uf.connected(0, 5));
        t.assertThrows("union with invalid index throws", IllegalArgumentException.class,
                () -> uf.union(0, 5));
        t.assertThrows("componentSize with invalid index throws", IllegalArgumentException.class,
                () -> uf.componentSize(5));
        t.assertThrows("componentMembers with invalid index throws", IllegalArgumentException.class,
                () -> uf.componentMembers(5));
    }
}
