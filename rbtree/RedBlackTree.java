package rbtree;

/**
 * A red-black tree supporting ordered integer insertion.
 *
 * <p>This is a faithful refactoring of the original single-file implementation.
 * The insertion algorithm is unchanged in behavior; the cleanups are structural:
 *
 * <ul>
 *   <li>Color is modeled with the {@link Color} enum instead of {@code char}
 *       sentinels.</li>
 *   <li>The four parallel boolean flags ({@code ll}, {@code rr}, {@code lr},
 *       {@code rl}) that signaled which rotation a parent frame must perform are
 *       collapsed into a single {@link RotationCase} field. At most one of the
 *       original flags was ever set, so a single-valued enum captures the same
 *       state without the risk of inconsistent combinations.</li>
 *   <li>The monolithic {@code insertHelp} is split into focused helpers:
 *       {@link #applyPendingRotation(Node)} performs a rotation requested by the
 *       child frame, and {@link #fixRedConflict(Node)} resolves a red-red
 *       violation by either recoloring or scheduling a rotation.</li>
 *   <li>Pretty-printing and in-order traversal output are delegated to
 *       {@link TreePrinter}.</li>
 * </ul>
 */
public class RedBlackTree {

    /**
     * The rotation a parent recursion frame must apply to rebalance after an
     * insertion. Set by {@link #fixRedConflict(Node)} on one frame and consumed
     * by {@link #applyPendingRotation(Node)} on the next frame up.
     */
    private enum RotationCase {
        NONE,
        /** Single left rotation (was the {@code ll} flag). */
        LEFT,
        /** Single right rotation (was the {@code rr} flag). */
        RIGHT,
        /** Right-then-left double rotation (was the {@code rl} flag). */
        RIGHT_LEFT,
        /** Left-then-right double rotation (was the {@code lr} flag). */
        LEFT_RIGHT
    }

    private Node root;
    private RotationCase pendingRotation = RotationCase.NONE;

    public RedBlackTree() {
        this.root = null;
    }

    /** Inserts a value, keeping the tree balanced and the root black. */
    public void insert(int data) {
        if (root == null) {
            root = new Node(data);
            root.color = Color.BLACK;
        } else {
            root = insertHelp(root, data);
        }
    }

    /** Prints the values in ascending order, space-separated. */
    public void inorderTraversal() {
        TreePrinter.inorder(root);
    }

    /** Prints a sideways, indented view of the tree structure. */
    public void printTree() {
        TreePrinter.print(root);
    }

    private Node rotateLeft(Node node) {
        Node pivot = node.right;
        Node orphan = pivot.left;
        pivot.left = node;
        node.right = orphan;
        node.parent = pivot;
        if (orphan != null) {
            orphan.parent = node;
        }
        return pivot;
    }

    private Node rotateRight(Node node) {
        Node pivot = node.left;
        Node orphan = pivot.right;
        pivot.right = node;
        node.left = orphan;
        node.parent = pivot;
        if (orphan != null) {
            orphan.parent = node;
        }
        return pivot;
    }

    /**
     * Recursively inserts {@code data} under {@code node}, returning the
     * (possibly new) subtree root. On the way back up it applies any rotation
     * requested by the child frame and detects red-red violations.
     */
    private Node insertHelp(Node node, int data) {
        if (node == null) {
            return new Node(data);
        }

        boolean redConflict = false;
        if (data < node.data) {
            node.left = insertHelp(node.left, data);
            node.left.parent = node;
            if (node != root && node.color == Color.RED && node.left.color == Color.RED) {
                redConflict = true;
            }
        } else {
            node.right = insertHelp(node.right, data);
            node.right.parent = node;
            if (node != root && node.color == Color.RED && node.right.color == Color.RED) {
                redConflict = true;
            }
        }

        node = applyPendingRotation(node);

        if (redConflict) {
            node = fixRedConflict(node);
        }
        return node;
    }

    /**
     * Applies the rotation, if any, that a child frame scheduled on this subtree
     * root, recoloring the rotated nodes as the original algorithm did. Clears
     * the pending request afterwards.
     */
    private Node applyPendingRotation(Node node) {
        switch (pendingRotation) {
            case LEFT:
                node = rotateLeft(node);
                node.color = Color.BLACK;
                node.left.color = Color.RED;
                break;
            case RIGHT:
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
            case NONE:
                break;
        }
        pendingRotation = RotationCase.NONE;
        return node;
    }

    /**
     * Resolves a red-red violation at {@code node}. Depending on the color of
     * the uncle, this either recolors locally or schedules a rotation on the
     * grandparent frame via {@link #pendingRotation}.
     */
    private Node fixRedConflict(Node node) {
        Node parent = node.parent;
        if (parent.right == node) {
            Node uncle = parent.left;
            if (uncle == null || uncle.color == Color.BLACK) {
                if (node.left != null && node.left.color == Color.RED) {
                    pendingRotation = RotationCase.RIGHT_LEFT;
                } else if (node.right != null && node.right.color == Color.RED) {
                    pendingRotation = RotationCase.LEFT;
                }
            } else {
                uncle.color = Color.BLACK;
                node.color = Color.BLACK;
                if (parent != root) {
                    parent.color = Color.RED;
                }
            }
        } else {
            Node uncle = parent.right;
            if (uncle == null || uncle.color == Color.BLACK) {
                if (node.left != null && node.left.color == Color.RED) {
                    pendingRotation = RotationCase.RIGHT;
                } else if (node.right != null && node.right.color == Color.RED) {
                    pendingRotation = RotationCase.LEFT_RIGHT;
                }
            } else {
                uncle.color = Color.BLACK;
                node.color = Color.BLACK;
                if (parent != root) {
                    parent.color = Color.RED;
                }
            }
        }
        return node;
    }
}
