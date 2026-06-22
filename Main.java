public final class Main {
    private static final int MIN_DEGREE = 3;
    private static final int[] SAMPLE_KEYS = {10, 20, 5, 6, 12, 30, 7, 17};

    public static void main(String[] args) {
        BTree tree = buildSampleTree();

        System.out.println("Traversal of the constructed tree is " + tree);

        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static BTree buildSampleTree() {
        BTree tree = new BTree(MIN_DEGREE);
        for (int key : SAMPLE_KEYS) {
            tree.insert(key);
        }
        return tree;
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
