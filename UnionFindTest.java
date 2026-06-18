public final class UnionFindTest {
    private UnionFindTest() {
    }

    public static void main(String[] args) {
        int testsRun = 0;

        testsRun += runTest("new elements start disconnected", UnionFindTest::testNewElementsStartDisconnected);
        testsRun += runTest("union connects elements", UnionFindTest::testUnionConnectsElements);
        testsRun += runTest("union reports merge status", UnionFindTest::testUnionReturnsWhetherMergeHappened);
        testsRun += runTest("component count tracks merges", UnionFindTest::testComponentCountTracksMerges);
        testsRun += runTest("component size tracks merges", UnionFindTest::testComponentSizeTracksMerges);
        testsRun += runTest("invalid elements are rejected", UnionFindTest::testInvalidElementsAreRejected);

        System.out.println("All " + testsRun + " UnionFind tests passed.");
    }

    private static void testNewElementsStartDisconnected() {
        DisjointSet unionFind = new UnionFind(3);

        assertFalse(unionFind.connected(0, 1), "new elements should start disconnected");
    }

    private static void testUnionConnectsElements() {
        DisjointSet unionFind = new UnionFind(4);

        unionFind.union(0, 1);
        unionFind.union(2, 3);
        unionFind.union(1, 3);

        assertTrue(unionFind.connected(0, 3), "transitive unions should connect elements");
    }

    private static void testUnionReturnsWhetherMergeHappened() {
        DisjointSet unionFind = new UnionFind(2);

        assertTrue(unionFind.union(0, 1), "first union should merge sets");
        assertFalse(unionFind.union(0, 1), "repeated union should not merge sets");
    }

    private static void testComponentCountTracksMerges() {
        DisjointSet unionFind = new UnionFind(4);

        assertEquals(4, unionFind.componentCount(), "initial component count");
        unionFind.union(0, 1);
        unionFind.union(2, 3);
        unionFind.union(1, 3);

        assertEquals(1, unionFind.componentCount(), "component count after merges");
        assertEquals(1, unionFind.components(), "components alias after merges");
    }

    private static void testComponentSizeTracksMerges() {
        DisjointSet unionFind = new UnionFind(5);

        assertEquals(1, unionFind.componentSize(0), "initial component size");
        unionFind.union(0, 1);
        unionFind.union(2, 3);

        assertEquals(2, unionFind.componentSize(0), "component size after first merge");
        assertEquals(2, unionFind.componentSize(3), "component size after second merge");

        unionFind.union(1, 2);

        assertEquals(4, unionFind.componentSize(0), "component size after joining components");
        assertEquals(1, unionFind.componentSize(4), "unmerged element size");
    }

    private static void testInvalidElementsAreRejected() {
        DisjointSet unionFind = new UnionFind(1);
        DisjointSet emptyUnionFind = new UnionFind(0);

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

    private static int runTest(String name, Runnable test) {
        try {
            test.run();
            return 1;
        } catch (AssertionError error) {
            throw new AssertionError(name + " failed: " + error.getMessage(), error);
        } catch (RuntimeException exception) {
            throw new AssertionError(name + " failed with unexpected exception", exception);
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
