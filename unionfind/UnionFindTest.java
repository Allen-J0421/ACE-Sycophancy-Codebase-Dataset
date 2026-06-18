package unionfind;

public class UnionFindTest {
    public static void main(String[] args) {
        TestSupport t = new TestSupport();

        // Initial state: each element is its own component
        UnionFind uf = new UnionFind(5);
        t.assertEquals("initial component count", 5, uf.getComponentCount());
        t.assertFalse("0 and 1 not connected initially", uf.connected(0, 1));

        // Basic union
        uf.union(0, 1);
        t.assertTrue("0 and 1 connected after union", uf.connected(0, 1));
        t.assertEquals("component count after one union", 4, uf.getComponentCount());

        // Transitivity
        uf.union(1, 2);
        t.assertTrue("0 and 2 connected transitively", uf.connected(0, 2));
        t.assertEquals("component count after two unions", 3, uf.getComponentCount());

        // Redundant union doesn't change component count
        uf.union(0, 2);
        t.assertEquals("redundant union doesn't reduce count", 3, uf.getComponentCount());

        // Separate component
        t.assertFalse("3 not connected to 0", uf.connected(0, 3));

        // Merge remaining components
        uf.union(3, 4);
        uf.union(2, 3);
        t.assertTrue("all elements connected", uf.connected(0, 4));
        t.assertEquals("final component count", 1, uf.getComponentCount());

        // Self-connection
        t.assertTrue("element connected to itself", uf.connected(0, 0));

        // Out-of-bounds throws
        t.assertThrows("find out-of-bounds throws", () -> uf.find(10));

        t.printSummary();
        if (!t.allPassed()) System.exit(1);
    }
}
