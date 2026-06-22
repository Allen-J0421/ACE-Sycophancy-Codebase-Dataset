public final class Main {
    public static void main(String[] args) {
        BTree tree = BTreeSamples.newDefaultTree();

        System.out.println("Traversal of the constructed tree is " + tree);

        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
