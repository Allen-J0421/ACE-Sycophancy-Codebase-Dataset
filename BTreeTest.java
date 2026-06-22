import java.util.Arrays;
import java.util.List;

public final class BTreeTest {
    public static void main(String[] args) {
        shouldRejectInvalidMinDegree();
        shouldReportEmptyState();
        shouldPreserveSortedTraversalAcrossInsertions();
        shouldSupportMembershipChecks();
    }

    private static void shouldRejectInvalidMinDegree() {
        boolean threw = false;
        try {
            new BTree(1);
        } catch (IllegalArgumentException exception) {
            threw = true;
        }

        assertTrue(threw, "Expected invalid minimum degree to be rejected.");
    }

    private static void shouldReportEmptyState() {
        BTree tree = new BTree(3);

        assertTrue(tree.isEmpty(), "Expected a new tree to be empty.");
        assertEquals("", tree.toString(), "Expected empty tree string representation to be blank.");
        assertEquals(List.of(), tree.toList(), "Expected empty tree traversal to have no keys.");
    }

    private static void shouldPreserveSortedTraversalAcrossInsertions() {
        BTree tree = new BTree(3);
        int[] insertedKeys = {10, 20, 5, 6, 12, 30, 7, 17};

        for (int key : insertedKeys) {
            tree.insert(key);
        }

        List<Integer> expectedKeys = Arrays.asList(5, 6, 7, 10, 12, 17, 20, 30);
        assertEquals(expectedKeys, tree.toList(), "Expected in-order traversal to stay sorted.");
        assertEquals("5 6 7 10 12 17 20 30", tree.toString(), "Expected stable string rendering.");
    }

    private static void shouldSupportMembershipChecks() {
        BTree tree = new BTree(2);
        int[] insertedKeys = {8, 9, 10, 11, 15, 20, 17};

        for (int key : insertedKeys) {
            tree.insert(key);
        }

        assertTrue(tree.contains(10), "Expected inserted key to be found.");
        assertTrue(tree.contains(17), "Expected inserted key to be found.");
        assertFalse(tree.contains(4), "Expected missing key to be absent.");
        assertFalse(tree.isEmpty(), "Expected populated tree to report non-empty.");
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
}
