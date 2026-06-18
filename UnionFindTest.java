public final class UnionFindTest {
    private UnionFindTest() {
    }

    public static void main(String[] args) {
        testNewElementsStartDisconnected();
        testUnionConnectsElements();
        testUnionReturnsWhetherMergeHappened();
        testComponentCountTracksMerges();
        testInvalidElementsAreRejected();

        System.out.println("All UnionFind tests passed.");
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
        UnionFind emptyUnionFind = new UnionFind(0);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> unionFind.find(-1),
                "negative element should be rejected"
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> unionFind.find(1),
                "element beyond size should be rejected"
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> emptyUnionFind.find(0),
                "empty union-find should reject every element"
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new UnionFind(-1),
                "negative size should be rejected"
        );
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

    private static void assertThrows(
            Class<? extends RuntimeException> expectedType,
            Runnable action,
            String message
    ) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (!expectedType.isInstance(exception)) {
                throw new AssertionError(
                        message + ": expected " + expectedType.getSimpleName()
                                + ", got " + exception.getClass().getSimpleName()
                );
            }

            return;
        }

        throw new AssertionError(message + ": expected " + expectedType.getSimpleName());
    }
}
