package disjointset;

public final class UnionFindTest {
    private UnionFindTest() {
    }

    public static void main(String[] args) {
        verifiesEmptyStructure();
        verifiesUnionAndConnectivity();
        verifiesComponentSizesAndCounts();
        verifiesIndexValidation();
    }

    private static void verifiesEmptyStructure() {
        DisjointSet disjointSet = DisjointSet.create(0);

        assertTrue(disjointSet.isEmpty(), "expected empty set to report isEmpty()");
        assertEquals(0, disjointSet.size(), "unexpected size for empty set");
        assertEquals(0, disjointSet.components(), "unexpected component count for empty set");
    }

    private static void verifiesUnionAndConnectivity() {
        DisjointSet disjointSet = DisjointSet.create(6);

        assertTrue(disjointSet.union(0, 1), "first union should merge two components");
        assertTrue(disjointSet.union(1, 2), "chained union should merge through compressed roots");
        assertFalse(disjointSet.union(0, 2), "union on same component should report no change");
        assertTrue(disjointSet.connected(0, 2), "elements 0 and 2 should be connected");
        assertFalse(disjointSet.connected(0, 3), "elements 0 and 3 should not be connected");
    }

    private static void verifiesComponentSizesAndCounts() {
        DisjointSet disjointSet = DisjointSet.create(5);

        disjointSet.union(0, 1);
        disjointSet.union(3, 4);

        assertEquals(3, disjointSet.components(), "unexpected component count after merges");
        assertEquals(2, disjointSet.componentSize(0), "unexpected component size for root component");
        assertEquals(2, disjointSet.componentSize(1), "component size should be consistent within a set");
        assertEquals(2, disjointSet.componentSize(3), "unexpected component size for second merged set");
        assertEquals(1, disjointSet.componentSize(2), "singleton component should remain size 1");
    }

    private static void verifiesIndexValidation() {
        DisjointSet disjointSet = DisjointSet.create(3);

        assertThrows(() -> disjointSet.find(-1), IndexOutOfBoundsException.class);
        assertThrows(() -> disjointSet.find(3), IndexOutOfBoundsException.class);
        assertThrows(() -> disjointSet.componentSize(4), IndexOutOfBoundsException.class);
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
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertThrows(ThrowingRunnable runnable, Class<? extends Throwable> expectedType) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(
                "expected exception " + expectedType.getSimpleName()
                    + " but caught "
                    + throwable.getClass().getSimpleName(),
                throwable
            );
        }

        throw new AssertionError("expected exception " + expectedType.getSimpleName());
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
