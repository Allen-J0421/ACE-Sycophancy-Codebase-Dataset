package rbtree;

/**
 * Rendering of a tree to standard output.
 *
 * <p>Separating presentation from the {@link RedBlackTree} keeps the data
 * structure free of I/O concerns. The output is identical to the original
 * {@code inorderTraversalHelper} / {@code printTreeHelper} methods.
 */
final class TreePrinter {

    /** Horizontal indentation, in spaces, added per level in {@link #print}. */
    private static final int INDENT = 10;

    private TreePrinter() {
    }

    /** Prints node values in ascending order, each followed by a space. */
    static void inorder(Node node) {
        if (node != null) {
            inorder(node.left);
            System.out.printf("%d ", node.data);
            inorder(node.right);
        }
    }

    /** Prints a sideways view of the tree (right subtree on top). */
    static void print(Node root) {
        print(root, 0);
    }

    private static void print(Node node, int space) {
        if (node == null) {
            return;
        }
        space += INDENT;
        print(node.right, space);

        System.out.printf("\n");
        for (int i = INDENT; i < space; i++) {
            System.out.printf(" ");
        }
        System.out.printf("%d", node.data);
        System.out.printf("\n");

        print(node.left, space);
    }
}
