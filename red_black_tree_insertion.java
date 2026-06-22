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
    enum Color { RED, BLACK }

    /**
     * Rotation case to be resolved one level up the recursive call stack.
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
    class Node {
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

    public Node root;
    private RotationCase pendingRotation = RotationCase.NONE;

    public RedBlackTree() {
        root = null;
    }

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
     */
    private Node insertHelp(Node node, int data) {
        boolean redConflict = false;

        if (node == null) {
            return new Node(data);
        } else if (data < node.data) {
            node.left = insertHelp(node.left, data);
            node.left.parent = node;
            if (node != this.root && node.color == Color.RED && node.left.color == Color.RED) {
                redConflict = true;
            }
        } else {
            node.right = insertHelp(node.right, data);
            node.right.parent = node;
            if (node != this.root && node.color == Color.RED && node.right.color == Color.RED) {
                redConflict = true;
            }
        }

        node = applyPendingRotation(node);

        if (redConflict) {
            pendingRotation = determineRotationCase(node);
        }

        return node;
    }

    /**
     * Applies the pending rotation (if any) to {@code node} to resolve a red-red conflict
     * signaled by a recursive call below.
     */
    private Node applyPendingRotation(Node node) {
        switch (pendingRotation) {
            case RIGHT_RIGHT:
                node = rotateLeft(node);
                node.color = Color.BLACK;
                node.left.color = Color.RED;
                pendingRotation = RotationCase.NONE;
                break;
            case LEFT_LEFT:
                node = rotateRight(node);
                node.color = Color.BLACK;
                node.right.color = Color.RED;
                pendingRotation = RotationCase.NONE;
                break;
            case RIGHT_LEFT:
                node.right = rotateRight(node.right);
                node.right.parent = node;
                node = rotateLeft(node);
                node.color = Color.BLACK;
                node.left.color = Color.RED;
                pendingRotation = RotationCase.NONE;
                break;
            case LEFT_RIGHT:
                node.left = rotateLeft(node.left);
                node.left.parent = node;
                node = rotateRight(node);
                node.color = Color.BLACK;
                node.right.color = Color.RED;
                pendingRotation = RotationCase.NONE;
                break;
            default:
                break;
        }
        return node;
    }

    /**
     * Examines the red-red conflict at {@code node} and either recolors (uncle-is-red case)
     * or returns the rotation case to apply one level up (uncle-is-black cases).
     */
    private RotationCase determineRotationCase(Node node) {
        Node parent = node.parent;

        if (parent.right == node) {
            Node uncle = parent.left;
            if (uncle == null || uncle.color == Color.BLACK) {
                if (node.left != null && node.left.color == Color.RED) {
                    return RotationCase.RIGHT_LEFT;
                } else if (node.right != null && node.right.color == Color.RED) {
                    return RotationCase.RIGHT_RIGHT;
                }
            } else {
                uncle.color = Color.BLACK;
                node.color = Color.BLACK;
                if (parent != this.root) {
                    parent.color = Color.RED;
                }
            }
        } else {
            Node uncle = parent.right;
            if (uncle == null || uncle.color == Color.BLACK) {
                if (node.left != null && node.left.color == Color.RED) {
                    return RotationCase.LEFT_LEFT;
                } else if (node.right != null && node.right.color == Color.RED) {
                    return RotationCase.LEFT_RIGHT;
                }
            } else {
                uncle.color = Color.BLACK;
                node.color = Color.BLACK;
                if (parent != this.root) {
                    parent.color = Color.RED;
                }
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
            this.root = insertHelp(this.root, data);
        }
    }

    private void inorderTraversalHelper(Node node) {
        if (node != null) {
            inorderTraversalHelper(node.left);
            System.out.printf("%d ", node.data);
            inorderTraversalHelper(node.right);
        }
    }

    /** Prints the values of all nodes in ascending sorted order. */
    public void inorderTraversal() {
        inorderTraversalHelper(this.root);
    }

    private void printTreeHelper(Node node, int indentLevel) {
        if (node != null) {
            int childIndent = indentLevel + 10;
            printTreeHelper(node.right, childIndent);
            System.out.println();
            for (int i = 10; i < childIndent; i++) {
                System.out.print(" ");
            }
            System.out.printf("%d%n", node.data);
            printTreeHelper(node.left, childIndent);
        }
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
