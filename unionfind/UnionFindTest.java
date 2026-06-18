package unionfind;

public class UnionFindTest {
    public static void main(String[] args) {
        TestSupport t = new TestSupport();
        testInitialState(t);
        testUnionAndConnectivity(t);
        testComponentCount(t);
        testBoundaryConditions(t);
        t.printSummary();
        if (!t.allPassed()) System.exit(1);
    }

    private static void testInitialState(TestSupport t) {
        UnionFind uf = new UnionFind(5);
        t.assertEquals("initial component count", 5, uf.getComponentCount());
        t.assertEquals("capacity", 5, uf.capacity());
        t.assertFalse("distinct elements not connected initially", uf.connected(0, 1));
        t.assertTrue("element is connected to itself", uf.connected(0, 0));
    }

    private static void testUnionAndConnectivity(TestSupport t) {
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
        t.assertEquals("starts with n components", 5, uf.getComponentCount());

        uf.union(0, 1);
        t.assertEquals("one union reduces count by one", 4, uf.getComponentCount());

        uf.union(1, 2);
        t.assertEquals("second union reduces count again", 3, uf.getComponentCount());

        uf.union(0, 2); // redundant — already in same component
        t.assertEquals("redundant union leaves count unchanged", 3, uf.getComponentCount());

        uf.union(3, 4);
        uf.union(2, 3);
        t.assertEquals("all merged into one component", 1, uf.getComponentCount());
    }

    private static void testBoundaryConditions(TestSupport t) {
        UnionFind single = new UnionFind(1);
        t.assertEquals("single-element structure has one component", 1, single.getComponentCount());
        t.assertTrue("single element is connected to itself", single.connected(0, 0));

        UnionFind uf = new UnionFind(5);
        t.assertThrows("find with negative index throws", () -> uf.find(-1));
        t.assertThrows("find with index equal to capacity throws", () -> uf.find(5));
        t.assertThrows("connected with invalid second index throws", () -> uf.connected(0, 5));
        t.assertThrows("union with invalid index throws", () -> uf.union(0, 5));
    }
}
