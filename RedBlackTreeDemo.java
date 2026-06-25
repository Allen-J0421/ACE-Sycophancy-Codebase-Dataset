public final class RedBlackTreeDemo {
    private RedBlackTreeDemo() {
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            if (!tree.isValidRedBlackTree()) {
                throw new IllegalStateException("red-black tree invariant violation");
            }
            System.out.println();
            System.out.print(tree.inorderString());
        }

        System.out.print(tree.treeString());
    }
}
