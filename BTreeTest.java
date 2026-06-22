import java.util.Arrays;
import java.util.List;

public final class BTreeTest {
    public static void main(String[] args) {
        BTreeTestSupport.runTest("rejects invalid minimum degree", BTreeTest::shouldRejectInvalidMinDegree);
        BTreeTestSupport.runTest("reports empty state", BTreeTest::shouldReportEmptyState);
        BTreeTestSupport.runTest("preserves sorted traversal", BTreeTest::shouldPreserveSortedTraversalAcrossInsertions);
        BTreeTestSupport.runTest("supports membership checks", BTreeTest::shouldSupportMembershipChecks);
        BTreeTestSupport.runTest("supports bulk insertion", BTreeTest::shouldSupportBulkInsertion);
    }

    private static void shouldRejectInvalidMinDegree() {
        BTreeTestSupport.expectThrows(IllegalArgumentException.class, () -> new BTree(1),
                "Expected invalid minimum degree to be rejected.");
    }

    private static void shouldReportEmptyState() {
        BTree tree = new BTree(3);

        BTreeTestSupport.assertTreeContents(tree, List.of());
    }

    private static void shouldPreserveSortedTraversalAcrossInsertions() {
        BTree tree = BTreeSamples.newDefaultTree();

        BTreeTestSupport.assertTreeContents(tree, defaultSortedKeys());
    }

    private static void shouldSupportMembershipChecks() {
        BTree tree = BTreeSamples.fromKeys(2, 8, 9, 10, 11, 15, 20, 17);

        BTreeTestSupport.assertContains(tree, 10);
        BTreeTestSupport.assertContains(tree, 17);
        BTreeTestSupport.assertDoesNotContain(tree, 4);
        BTreeTestSupport.assertTreeContents(tree, Arrays.asList(8, 9, 10, 11, 15, 17, 20));
    }

    private static void shouldSupportBulkInsertion() {
        BTree tree = BTree.fromKeys(3, 10, 20, 5, 6, 12, 30, 7, 17);

        BTreeTestSupport.assertTreeContents(tree, defaultSortedKeys());
    }

    private static List<Integer> defaultSortedKeys() {
        return Arrays.asList(5, 6, 7, 10, 12, 17, 20, 30);
    }
}
