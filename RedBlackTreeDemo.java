public final class RedBlackTreeDemo {
    private RedBlackTreeDemo() {
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            System.out.println();
            tree.inorderTraversal();
            System.out.println();
        }

        tree.printTree();
    }
}
