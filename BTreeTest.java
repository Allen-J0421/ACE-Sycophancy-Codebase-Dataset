import java.util.Arrays;
import java.util.List;

public final class BTreeTest {
    public static void main(String[] args) {
        runTest("rejects invalid minimum degree", BTreeTest::shouldRejectInvalidMinDegree);
        runTest("reports empty state", BTreeTest::shouldReportEmptyState);
        runTest("preserves sorted traversal", BTreeTest::shouldPreserveSortedTraversalAcrossInsertions);
        runTest("supports membership checks", BTreeTest::shouldSupportMembershipChecks);
        runTest("supports bulk insertion", BTreeTest::shouldSupportBulkInsertion);
    }

    private static void shouldRejectInvalidMinDegree() {
        expectThrows(IllegalArgumentException.class, () -> new BTree(1),
                "Expected invalid minimum degree to be rejected.");
    }

    private static void shouldReportEmptyState() {
        BTree tree = new BTree(3);

        assertTreeState(tree, List.of(), "", true);
    }

    private static void shouldPreserveSortedTraversalAcrossInsertions() {
        BTree tree = BTreeSamples.newDefaultTree();

        assertTreeState(tree, defaultSortedKeys(), "5 6 7 10 12 17 20 30", false);
    }

    private static void shouldSupportMembershipChecks() {
        BTree tree = BTreeSamples.fromKeys(2, 8, 9, 10, 11, 15, 20, 17);

        assertTrue(tree.contains(10), "Expected inserted key to be found.");
        assertTrue(tree.contains(17), "Expected inserted key to be found.");
        assertFalse(tree.contains(4), "Expected missing key to be absent.");
        assertFalse(tree.isEmpty(), "Expected populated tree to report non-empty.");
    }

    private static void shouldSupportBulkInsertion() {
        BTree tree = new BTree(3);
        tree.insertAll(10, 20, 5, 6, 12, 30, 7, 17);

        assertTreeState(tree, defaultSortedKeys(), "5 6 7 10 12 17 20 30", false);
    }

    private static List<Integer> defaultSortedKeys() {
        return Arrays.asList(5, 6, 7, 10, 12, 17, 20, 30);
    }

    private static void assertTreeState(BTree tree, List<Integer> expectedKeys, String expectedText, boolean empty) {
        assertEquals(expectedKeys, tree.toList(), "Unexpected in-order traversal.");
        assertEquals(expectedText, tree.toString(), "Unexpected string representation.");
        assertEquals(empty, tree.isEmpty(), "Unexpected empty-state result.");
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + " Expected exception type: " + expectedType.getSimpleName()
                    + ", Actual: " + throwable.getClass().getSimpleName(), throwable);
        }

        throw new AssertionError(message);
    }

    private static void runTest(String name, Runnable test) {
        try {
            test.run();
        } catch (Throwable throwable) {
            throw new AssertionError("Test failed: " + name, throwable);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
