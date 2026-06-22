package rbtree;

/**
 * A red-black tree supporting ordered insertion of any {@link Comparable} type.
 *
 * <p>This is a faithful refactoring of the original single-file implementation.
 * The insertion algorithm is unchanged in behavior; the cleanups are structural:
 *
 * <ul>
 *   <li>The tree is generic over {@code T extends Comparable<T>} rather than
 *       hardcoded to {@code int}. Ordering uses {@link Comparable#compareTo},
 *       which reduces to the original {@code <} comparison for integers.</li>
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
 *
 * @param <T> the type of value stored in the tree
 */
public class RedBlackTree<T extends Comparable<T>> {

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

    private Node<T> root;
    private RotationCase pendingRotation = RotationCase.NONE;

    public RedBlackTree() {
        this.root = null;
    }

    /** Inserts a value, keeping the tree balanced and the root black. */
    public void insert(T data) {
        if (root == null) {
            root = new Node<>(data);
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

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> pivot = node.right;
        Node<T> orphan = pivot.left;
        pivot.left = node;
        node.right = orphan;
        node.parent = pivot;
        if (orphan != null) {
            orphan.parent = node;
        }
        return pivot;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> pivot = node.left;
        Node<T> orphan = pivot.right;
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
    private Node<T> insertHelp(Node<T> node, T data) {
        if (node == null) {
            return new Node<>(data);
        }

        boolean redConflict = false;
        if (data.compareTo(node.data) < 0) {
            node.left = insertHelp(node.left, data);
            node.left.parent = node;
            redConflict = node != root && isRed(node) && isRed(node.left);
        } else {
            node.right = insertHelp(node.right, data);
            node.right.parent = node;
            redConflict = node != root && isRed(node) && isRed(node.right);
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
    private Node<T> applyPendingRotation(Node<T> node) {
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
    private Node<T> fixRedConflict(Node<T> node) {
        Node<T> parent = node.parent;
        if (parent.right == node) {
            Node<T> uncle = parent.left;
            if (isRed(uncle)) {
                recolorWithRedUncle(node, uncle, parent);
            } else if (isRed(node.left)) {
                pendingRotation = RotationCase.RIGHT_LEFT;
            } else if (isRed(node.right)) {
                pendingRotation = RotationCase.LEFT;
            }
        } else {
            Node<T> uncle = parent.right;
            if (isRed(uncle)) {
                recolorWithRedUncle(node, uncle, parent);
            } else if (isRed(node.left)) {
                pendingRotation = RotationCase.RIGHT;
            } else if (isRed(node.right)) {
                pendingRotation = RotationCase.LEFT_RIGHT;
            }
        }
        return node;
    }

    /**
     * Resolves a red-red violation when the uncle is also red: both the node and
     * the uncle are blackened and the grandparent is reddened (unless it is the
     * root, which must stay black), pushing the potential violation upward.
     */
    private void recolorWithRedUncle(Node<T> node, Node<T> uncle, Node<T> parent) {
        uncle.color = Color.BLACK;
        node.color = Color.BLACK;
        if (parent != root) {
            parent.color = Color.RED;
        }
    }

    /** Null-safe redness test; a {@code null} (leaf) node is treated as black. */
    private static boolean isRed(Node<?> node) {
        return node != null && node.color == Color.RED;
    }
}
