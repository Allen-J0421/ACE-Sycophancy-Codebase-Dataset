public final class Main {
    public static void main(String[] args) {
        BTreeScenario scenario = BTreeSamples.defaultScenario();
        BTree tree = scenario.createTree();

        System.out.println("Traversal of the constructed tree is " + tree);

        printSearchResults(tree, scenario.presentKeys());
        printSearchResults(tree, scenario.missingKeys());
    }

    private static void printSearchResults(BTree tree, Iterable<Integer> keys) {
        for (int key : keys) {
            if (tree.contains(key)) {
                System.out.println(" | Present");
            } else {
                System.out.println(" | Not Present");
            }
        }
    }
}
