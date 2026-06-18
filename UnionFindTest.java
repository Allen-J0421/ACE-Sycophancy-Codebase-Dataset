public class UnionFindTest {

    public static void main(String[] args) {
        testBasicUnion();
        testConnectivity();
        testMultipleUnions();
        testValidation();
        testStatistics();
        testReset();
        testBuilder();
        System.out.println("\n✓ All tests passed");
    }

    private static void testBasicUnion() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(1, 2) : "1 and 2 should be connected";
        assert !uf.isConnected(2, 4) : "2 and 4 should not be connected";
        System.out.println("✓ testBasicUnion passed");
    }

    private static void testConnectivity() {
        UnionFind uf = UnionFind.builder().withSize(6).build();
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(0, 2) : "0 and 2 should be transitively connected";
        assert !uf.isConnected(2, 3) : "2 and 3 should not be connected";
        System.out.println("✓ testConnectivity passed");
    }

    private static void testMultipleUnions() {
        UnionFind uf = UnionFind.builder().withSize(10).build();
        uf.union(1, 2);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(5, 6);
        uf.union(3, 4);
        assert uf.isConnected(1, 6) : "1 and 6 should be connected after union chain";
        System.out.println("✓ testMultipleUnions passed");
    }

    private static void testValidation() {
        testInvalidIndex();
        testInvalidSize();
        System.out.println("✓ testValidation passed");
    }

    private static void testInvalidIndex() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        try {
            uf.find(-1);
            assert false : "Should throw IndexOutOfBoundsException";
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            uf.union(0, 5);
            assert false : "Should throw IndexOutOfBoundsException";
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    private static void testInvalidSize() {
        try {
            UnionFind.builder().withSize(0).build();
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            UnionFind.builder().withSize(-1).build();
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    private static void testStatistics() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(2, 3);
        uf.isConnected(1, 3);
        DisjointSet.Statistics stats = uf.getStatistics();
        assert stats.unionOperations == 2 : "Should have 2 union operations";
        assert stats.findOperations >= 3 : "Should have at least 3 find operations";
        System.out.println("✓ testStatistics passed (" + stats + ")");
    }

    private static void testReset() {
        UnionFind uf = UnionFind.builder().withSize(5).build();
        uf.union(1, 2);
        uf.union(2, 3);
        DisjointSet.Statistics statsBefore = uf.getStatistics();
        assert statsBefore.unionOperations == 2 : "Should have recorded operations";

        uf.reset();
        DisjointSet.Statistics statsAfter = uf.getStatistics();
        assert statsAfter.findOperations == 0 : "Statistics should be reset";
        assert statsAfter.unionOperations == 0 : "Statistics should be reset";
        assert !uf.isConnected(1, 2) : "Connections should be reset";
        System.out.println("✓ testReset passed");
    }

    private static void testBuilder() {
        try {
            UnionFind.builder().build();
            assert false : "Should throw IllegalArgumentException when size not set";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            UnionFind.builder().withSize(0).build();
            assert false : "Should throw IllegalArgumentException for non-positive size";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        UnionFind uf = UnionFind.builder().withSize(10).build();
        assert uf.getSize() == 10 : "Builder should create UnionFind with correct size";
        System.out.println("✓ testBuilder passed");
    }
}
