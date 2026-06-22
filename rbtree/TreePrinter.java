package rbtree;

import java.util.List;

/**
 * Rendering of a tree to standard output.
 *
 * <p>Separating presentation from the {@link RedBlackTree} keeps the data
 * structure free of I/O concerns. Values are printed via their {@code toString}
 * representation, so the printer is agnostic to the tree's element type. The
 * output is identical to the original {@code inorderTraversalHelper} /
 * {@code printTreeHelper} methods.
 */
final class TreePrinter {

    /** Horizontal indentation, in spaces, added per level in {@link #print}. */
    private static final int INDENT = 10;

    private TreePrinter() {
    }

    /** Prints the given values in order, each followed by a space. */
    static void printValues(List<?> values) {
        for (Object value : values) {
            System.out.printf("%s ", value);
        }
    }

    /** Prints a sideways view of the tree (right subtree on top). */
    static void print(Node<?> root) {
        print(root, 0);
    }

    private static void print(Node<?> node, int space) {
        if (node == null) {
            return;
        }
        space += INDENT;
        print(node.right, space);

        String indent = " ".repeat(space - INDENT);
        System.out.print("\n" + indent + node.data + "\n");

        print(node.left, space);
    }
}
