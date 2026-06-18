package unionfind;

public final class UnionFindTest {
    public static void main(String[] args) {
        shouldTrackConnectivity();
        shouldTrackComponentCount();
        shouldRejectInvalidElements();
        System.out.println("All UnionFind checks passed.");
    }

    private static void shouldTrackConnectivity() {
        UnionFind unionFind = new UnionFind(4);

        TestSupport.assertFalse(unionFind.connected(0, 1), "0 and 1 should start disconnected");
        TestSupport.assertTrue(unionFind.union(0, 1), "first union should merge distinct sets");
        TestSupport.assertTrue(unionFind.connected(0, 1), "0 and 1 should be connected after union");
        TestSupport.assertFalse(unionFind.union(0, 1), "repeating the same union should not merge again");
    }

    private static void shouldTrackComponentCount() {
        UnionFind unionFind = new UnionFind(5);

        TestSupport.assertEquals(5, unionFind.elementCount(), "element count should match constructor size");
        TestSupport.assertEquals(5, unionFind.componentCount(), "initial component count");
        unionFind.union(0, 1);
        unionFind.union(2, 3);
        TestSupport.assertEquals(3, unionFind.componentCount(), "component count after two merges");
        unionFind.union(1, 3);
        TestSupport.assertEquals(2, unionFind.componentCount(), "component count after linking two components");
    }

    private static void shouldRejectInvalidElements() {
        UnionFind unionFind = new UnionFind(2);

        TestSupport.assertThrows(IndexOutOfBoundsException.class, () -> unionFind.find(-1));
        TestSupport.assertThrows(IndexOutOfBoundsException.class, () -> unionFind.union(0, 2));
    }
}
