package rbtree;

/**
 * Demonstration driver: inserts a fixed sequence of values, printing the
 * in-order traversal after each insertion and the tree shape at the end.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            System.out.println();
            tree.inorderTraversal();
        }

        tree.printTree();
    }
}
