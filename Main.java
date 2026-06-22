final class Main {
    public static void main(String[] args) {
        BTree tree = createDemoTree();
        printTraversal(tree);
        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static BTree createDemoTree() {
        BTree tree = new BTree(3);
        int[] values = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int value : values) {
            tree.insert(value);
        }
        return tree;
    }

    private static void printTraversal(BTree tree) {
        System.out.print("Traversal of the constructed tree is ");
        System.out.print(tree.traversalString());
        System.out.println();
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
