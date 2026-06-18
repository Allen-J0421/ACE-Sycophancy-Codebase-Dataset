public class UnionFindTest {

    public static void main(String[] args) {
        testBasicUnion();
        testConnectivity();
        testMultipleUnions();
        testValidation();
        System.out.println("\n✓ All tests passed");
    }

    private static void testBasicUnion() {
        UnionFind uf = new UnionFind(5);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(1, 2) : "1 and 2 should be connected";
        assert !uf.isConnected(2, 4) : "2 and 4 should not be connected";
        System.out.println("✓ testBasicUnion passed");
    }

    private static void testConnectivity() {
        UnionFind uf = new UnionFind(6);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        assert uf.isConnected(0, 2) : "0 and 2 should be transitively connected";
        assert !uf.isConnected(2, 3) : "2 and 3 should not be connected";
        System.out.println("✓ testConnectivity passed");
    }

    private static void testMultipleUnions() {
        UnionFind uf = new UnionFind(10);
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
        UnionFind uf = new UnionFind(5);
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
            new UnionFind(0);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new UnionFind(-1);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}
