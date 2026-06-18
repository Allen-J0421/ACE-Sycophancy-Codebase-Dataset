public class UnionFindTest {
    public static void main(String[] args) {
        testNewElementsStartDisconnected();
        testUnionConnectsElements();
        testUnionReturnsWhetherMergeHappened();
        testComponentCountTracksMerges();
        testInvalidElementsAreRejected();
    }

    private static void testNewElementsStartDisconnected() {
        UnionFind unionFind = new UnionFind(3);

        assertFalse(unionFind.connected(0, 1), "new elements should start disconnected");
    }

    private static void testUnionConnectsElements() {
        UnionFind unionFind = new UnionFind(4);

        unionFind.union(0, 1);
        unionFind.union(2, 3);
        unionFind.union(1, 3);

        assertTrue(unionFind.connected(0, 3), "transitive unions should connect elements");
    }

    private static void testUnionReturnsWhetherMergeHappened() {
        UnionFind unionFind = new UnionFind(2);

        assertTrue(unionFind.union(0, 1), "first union should merge sets");
        assertFalse(unionFind.union(0, 1), "repeated union should not merge sets");
    }

    private static void testComponentCountTracksMerges() {
        UnionFind unionFind = new UnionFind(4);

        assertEquals(4, unionFind.components(), "initial component count");
        unionFind.union(0, 1);
        unionFind.union(2, 3);
        unionFind.union(1, 3);

        assertEquals(1, unionFind.components(), "component count after merges");
    }

    private static void testInvalidElementsAreRejected() {
        UnionFind unionFind = new UnionFind(1);

        assertThrows(() -> unionFind.find(-1), "negative element should be rejected");
        assertThrows(() -> unionFind.find(1), "element beyond size should be rejected");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertThrows(Runnable action, String message) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            return;
        }

        throw new AssertionError(message);
    }
}
