/**
 * Self-balancing binary search tree that maintains the red-black tree invariants:
 * 1. Every node is RED or BLACK.
 * 2. The root is BLACK.
 * 3. Every null leaf is implicitly BLACK.
 * 4. If a node is RED, both its children are BLACK.
 * 5. All paths from any node to its descendant null leaves contain the same number of BLACK nodes.
 */
public class RedBlackTree {

    /** Color of a red-black tree node. */
    private enum Color { RED, BLACK }

    /**
     * Rotation case to be signaled from a recursive call back to its caller.
     * Named after the standard imbalance patterns in balanced BST literature.
     */
    private enum RotationCase {
        NONE,
        /** Both the conflicting node and its red grandchild are right children → single left rotation. */
        RIGHT_RIGHT,
        /** Both the conflicting node and its red grandchild are left children → single right rotation. */
        LEFT_LEFT,
        /** Conflicting node is a right child but its red grandchild is a left child → right-left double rotation. */
        RIGHT_LEFT,
        /** Conflicting node is a left child but its red grandchild is a right child → left-right double rotation. */
        LEFT_RIGHT
    }

    /** A node in the red-black tree. */
    private static class Node {
        int data;
        Node left;
        Node right;
        Color color;
        Node parent;

        Node(int data) {
            this.data = data;
            this.color = Color.RED;
        }
    }

    /** Return value of {@link #insertHelp}: the (possibly rotated) subtree root plus any pending fix for the caller. */
    private static final class InsertResult {
        final Node node;
        final RotationCase pendingRotation;

        InsertResult(Node node, RotationCase pendingRotation) {
            this.node = node;
            this.pendingRotation = pendingRotation;
        }
    }

    private static final int PRINT_INDENT_STEP = 10;

    private Node root;

    public RedBlackTree() {}

    /**
     * Rotates the subtree rooted at {@code pivot} to the left.
     * The right child of {@code pivot} becomes the new subtree root.
     */
    private Node rotateLeft(Node pivot) {
        Node newRoot = pivot.right;
        Node movedChild = newRoot.left;
        newRoot.left = pivot;
        pivot.right = movedChild;
        pivot.parent = newRoot;
        if (movedChild != null) {
            movedChild.parent = pivot;
        }
        return newRoot;
    }

    /**
     * Rotates the subtree rooted at {@code pivot} to the right.
     * The left child of {@code pivot} becomes the new subtree root.
     */
    private Node rotateRight(Node pivot) {
        Node newRoot = pivot.left;
        Node movedChild = newRoot.right;
        newRoot.right = pivot;
        pivot.left = movedChild;
        pivot.parent = newRoot;
        if (movedChild != null) {
            movedChild.parent = pivot;
        }
        return newRoot;
    }

    /**
     * Recursively inserts {@code data} into the subtree rooted at {@code node},
     * then fixes any red-black violations on the way back up the call stack.
     * Any rotation needed one level higher is communicated via {@link InsertResult#pendingRotation}.
     */
    private InsertResult insertHelp(Node node, int data) {
        if (node == null) {
            return new InsertResult(new Node(data), RotationCase.NONE);
        }

        final InsertResult childResult;
        final boolean redConflict;

        if (data < node.data) {
            childResult = insertHelp(node.left, data);
            node.left = childResult.node;
            node.left.parent = node;
            redConflict = node != this.root && node.color == Color.RED && node.left.color == Color.RED;
        } else {
            childResult = insertHelp(node.right, data);
            node.right = childResult.node;
            node.right.parent = node;
            redConflict = node != this.root && node.color == Color.RED && node.right.color == Color.RED;
        }

        node = applyPendingRotation(node, childResult.pendingRotation);
        RotationCase outgoing = redConflict ? determineRotationCase(node) : RotationCase.NONE;
        return new InsertResult(node, outgoing);
    }

    /**
     * Applies {@code pending} rotation to {@code node} to resolve a red-red conflict
     * that was signaled by a recursive call below.
     */
    private Node applyPendingRotation(Node node, RotationCase pending) {
        switch (pending) {
            case RIGHT_RIGHT:
                node = rotateLeft(node);
                node.color = Color.BLACK;
                node.left.color = Color.RED;
                break;
            case LEFT_LEFT:
                node = rotateRight(node);
                node.color = Color.BLACK;
                node.right.color = Color.RED;
                break;
            case RIGHT_LEFT:
                node.right = rotateRight(node.right);
                node.right.parent = node;
                node = rotateLeft(node);
                node.color = Color.BLACK;
                node.left.color = Color.RED;
                break;
            case LEFT_RIGHT:
                node.left = rotateLeft(node.left);
                node.left.parent = node;
                node = rotateRight(node);
                node.color = Color.BLACK;
                node.right.color = Color.RED;
                break;
        }
        return node;
    }

    /**
     * Examines the red-red conflict at {@code node} and either recolors (uncle-is-red case)
     * or returns the rotation case to apply at the grandparent level (uncle-is-black cases).
     */
    private RotationCase determineRotationCase(Node node) {
        Node parent = node.parent;
        boolean isRightChild = parent.right == node;
        Node uncle = isRightChild ? parent.left : parent.right;

        if (uncle == null || uncle.color == Color.BLACK) {
            boolean leftChildRed = node.left != null && node.left.color == Color.RED;
            boolean rightChildRed = node.right != null && node.right.color == Color.RED;
            if (isRightChild) {
                if (leftChildRed) return RotationCase.RIGHT_LEFT;
                if (rightChildRed) return RotationCase.RIGHT_RIGHT;
            } else {
                if (leftChildRed) return RotationCase.LEFT_LEFT;
                if (rightChildRed) return RotationCase.LEFT_RIGHT;
            }
        } else {
            uncle.color = Color.BLACK;
            node.color = Color.BLACK;
            if (parent != this.root) {
                parent.color = Color.RED;
            }
        }
        return RotationCase.NONE;
    }

    /**
     * Inserts {@code data} into the tree, maintaining red-black tree properties.
     */
    public void insert(int data) {
        if (this.root == null) {
            this.root = new Node(data);
            this.root.color = Color.BLACK;
        } else {
            this.root = insertHelp(this.root, data).node;
        }
    }

    private void inorderTraversalHelper(Node node) {
        if (node == null) return;
        inorderTraversalHelper(node.left);
        System.out.printf("%d ", node.data);
        inorderTraversalHelper(node.right);
    }

    /** Prints the values of all nodes in ascending sorted order. */
    public void inorderTraversal() {
        inorderTraversalHelper(this.root);
    }

    private void printTreeHelper(Node node, int indentLevel) {
        if (node == null) return;
        printTreeHelper(node.right, indentLevel + PRINT_INDENT_STEP);
        System.out.println();
        System.out.print(" ".repeat(indentLevel));
        System.out.printf("%d%n", node.data);
        printTreeHelper(node.left, indentLevel + PRINT_INDENT_STEP);
    }

    /** Prints the tree rotated 90° counter-clockwise so the right subtree appears at the top. */
    public void printTree() {
        printTreeHelper(this.root, 0);
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
