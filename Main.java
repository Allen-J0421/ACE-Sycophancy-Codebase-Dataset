public final class Main {
    public static void main(String[] args) {
        BTreeScenario scenario = BTreeSamples.defaultScenario();
        BTree tree = scenario.createTree();

        System.out.println("Traversal of the constructed tree is " + tree);

        printSearchResults(tree, scenario.searchExpectations());
    }

    private static void printSearchResults(BTree tree, Iterable<BTreeScenario.SearchExpectation> expectations) {
        for (BTreeScenario.SearchExpectation expectation : expectations) {
            System.out.println(" | " + expectation.actualStatusLabel(tree));
        }
    }
}
