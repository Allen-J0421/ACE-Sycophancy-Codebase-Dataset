package unionfind;

public class UnionFindTest {
    public static void main(String[] args) {
        TestSupport t = new TestSupport();
        testInitialState(t);
        testUnionReturnValue(t);
        testConnectivity(t);
        testComponentCount(t);
        testComponentSize(t);
        testBoundaryConditions(t);
        t.printSummary();
        if (!t.allPassed()) System.exit(1);
    }

    private static void testInitialState(TestSupport t) {
        t.section("Initial state");
        UnionFind uf = new UnionFind(5);
        t.assertEquals("component count starts at n", 5, uf.getComponentCount());
        t.assertEquals("capacity", 5, uf.capacity());
        t.assertFalse("distinct elements not connected initially", uf.connected(0, 1));
        t.assertTrue("element is connected to itself", uf.connected(0, 0));
        t.assertEquals("initial component size is 1", 1, uf.componentSize(0));
    }

    private static void testUnionReturnValue(TestSupport t) {
        t.section("union() return value");
        UnionFind uf = new UnionFind(5);
        t.assertTrue("union of disjoint elements returns true", uf.union(0, 1));
        t.assertFalse("union of already-connected elements returns false", uf.union(0, 1));
        uf.union(1, 2);
        t.assertFalse("union via transitive connection returns false", uf.union(0, 2));
    }

    private static void testConnectivity(TestSupport t) {
        t.section("Connectivity");
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
        t.section("Component count");
        UnionFind uf = new UnionFind(5);
        t.assertEquals("starts with n components", 5, uf.getComponentCount());

        uf.union(0, 1);
        t.assertEquals("one union reduces count by one", 4, uf.getComponentCount());

        uf.union(1, 2);
        t.assertEquals("second union reduces count again", 3, uf.getComponentCount());

        uf.union(0, 2); // redundant
        t.assertEquals("redundant union leaves count unchanged", 3, uf.getComponentCount());

        uf.union(3, 4);
        uf.union(2, 3);
        t.assertEquals("all merged into one component", 1, uf.getComponentCount());
    }

    private static void testComponentSize(TestSupport t) {
        t.section("Component size");
        UnionFind uf = new UnionFind(5);
        t.assertEquals("singleton component has size 1", 1, uf.componentSize(0));

        uf.union(0, 1);
        t.assertEquals("component size after merging 2", 2, uf.componentSize(0));
        t.assertEquals("both members report same size", uf.componentSize(0), uf.componentSize(1));

        uf.union(1, 2);
        t.assertEquals("component size after merging 3", 3, uf.componentSize(2));

        uf.union(3, 4);
        t.assertEquals("separate component has its own size", 2, uf.componentSize(3));
    }

    private static void testBoundaryConditions(TestSupport t) {
        t.section("Boundary conditions");
        UnionFind single = new UnionFind(1);
        t.assertEquals("single-element structure has one component", 1, single.getComponentCount());
        t.assertTrue("single element is connected to itself", single.connected(0, 0));
        t.assertEquals("single element has component size 1", 1, single.componentSize(0));

        UnionFind empty = new UnionFind(0);
        t.assertEquals("zero-capacity structure has zero components", 0, empty.getComponentCount());

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
    }
}
