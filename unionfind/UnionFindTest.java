package unionfind;

public final class UnionFindTest {
    public static void main(String[] args) {
        runTest("connectivity", UnionFindTest::shouldTrackConnectivity);
        runTest("component count", UnionFindTest::shouldTrackComponentCount);
        runTest("invalid elements", UnionFindTest::shouldRejectInvalidElements);
        System.out.println("All UnionFind checks passed.");
    }

    private static void runTest(String name, ThrowingRunnable test) {
        try {
            test.run();
        } catch (Throwable thrown) {
            throw new AssertionError("failed test: " + name, thrown);
        }
    }

    private static void shouldTrackConnectivity() {
        UnionFind unionFind = new UnionFind(4);

        assertFalse(unionFind.connected(0, 1), "0 and 1 should start disconnected");
        assertTrue(unionFind.union(0, 1), "first union should merge distinct sets");
        assertTrue(unionFind.connected(0, 1), "0 and 1 should be connected after union");
        assertFalse(unionFind.union(0, 1), "repeating the same union should not merge again");
    }

    private static void shouldTrackComponentCount() {
        UnionFind unionFind = new UnionFind(5);

        assertEquals(5, unionFind.elementCount(), "element count should match constructor size");
        assertEquals(5, unionFind.componentCount(), "initial component count");
        unionFind.union(0, 1);
        unionFind.union(2, 3);
        assertEquals(3, unionFind.componentCount(), "component count after two merges");
        unionFind.union(1, 3);
        assertEquals(2, unionFind.componentCount(), "component count after linking two components");
    }

    private static void shouldRejectInvalidElements() {
        UnionFind unionFind = new UnionFind(2);

        assertThrows(IndexOutOfBoundsException.class, () -> unionFind.find(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> unionFind.union(0, 2));
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable test) {
        try {
            test.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                    "expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName(),
                    thrown);
        }

        throw new AssertionError("expected " + expectedType.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
