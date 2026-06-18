public class UnionFindTest {
    public static void main(String[] args) {
        shouldTrackConnectivity();
        shouldTrackComponentCount();
        shouldRejectInvalidElements();
        System.out.println("All UnionFind checks passed.");
    }

    private static void shouldTrackConnectivity() {
        UnionFind unionFind = new UnionFind(4);

        expectFalse(unionFind.connected(0, 1), "0 and 1 should start disconnected");
        expectTrue(unionFind.union(0, 1), "first union should merge distinct sets");
        expectTrue(unionFind.connected(0, 1), "0 and 1 should be connected after union");
        expectFalse(unionFind.union(0, 1), "repeating the same union should not merge again");
    }

    private static void shouldTrackComponentCount() {
        UnionFind unionFind = new UnionFind(5);

        expectEquals(5, unionFind.componentCount(), "initial component count");
        unionFind.union(0, 1);
        unionFind.union(2, 3);
        expectEquals(3, unionFind.componentCount(), "component count after two merges");
        unionFind.union(1, 3);
        expectEquals(2, unionFind.componentCount(), "component count after linking two components");
    }

    private static void shouldRejectInvalidElements() {
        UnionFind unionFind = new UnionFind(2);

        expectThrows(IndexOutOfBoundsException.class, () -> unionFind.find(-1));
        expectThrows(IndexOutOfBoundsException.class, () -> unionFind.union(0, 2));
    }

    private static void expectTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action) {
        try {
            action.run();
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
