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
        BTreeTestSupport.assertMatchesScenario(BTreeSamples.defaultScenario());
    }

    private static void shouldSupportMembershipChecks() {
        BTreeTestSupport.assertMatchesScenario(BTreeSamples.membershipScenario());
    }

    private static void shouldSupportBulkInsertion() {
        BTreeScenario scenario = BTreeSamples.defaultScenario();
        BTree tree = new BTree(scenario.minDegree());
        tree.insertAll(scenario.insertedKeys());

        BTreeTestSupport.assertTreeContents(tree, scenario.expectedKeys());
    }
}
