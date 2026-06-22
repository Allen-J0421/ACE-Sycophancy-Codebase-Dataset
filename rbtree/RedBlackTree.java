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
 *       collapsed into a single {@link RotationCase}. At most one of the original
 *       flags was ever set, so a single-valued enum captures the same state
 *       without the risk of inconsistent combinations.</li>
 *   <li>That rotation request is threaded up the recursion as part of the
 *       {@link InsertResult} return value rather than stashed in a mutable
 *       instance field, so a tree holds no transient per-insertion state.</li>
 *   <li>The monolithic {@code insertHelp} is split into focused helpers:
 *       {@link #applyRotation(Node, RotationCase)} performs a rotation requested
 *       by the child frame, and {@link #scheduleRotationFor(Node)} resolves a
 *       red-red violation by either recoloring or scheduling a rotation.</li>
 *   <li>Pretty-printing and in-order traversal output are delegated to
 *       {@link TreePrinter}.</li>
 * </ul>
 *
 * @param <T> the type of value stored in the tree
 */
public class RedBlackTree<T extends Comparable<T>> {

    /**
     * The rotation a parent recursion frame must apply to rebalance after an
     * insertion. Produced by {@link #scheduleRotationFor(Node)} on one frame and
     * consumed by {@link #applyRotation(Node, RotationCase)} on the next frame up.
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

    /**
     * Result of one {@link #insertHelp} frame: the (possibly new) subtree root
     * and the rotation, if any, that the parent frame must apply to rebalance.
     */
    private static final class InsertResult<T> {
        final Node<T> node;
        final RotationCase rotation;

        InsertResult(Node<T> node, RotationCase rotation) {
            this.node = node;
            this.rotation = rotation;
        }
    }

    private Node<T> root;

    public RedBlackTree() {
        this.root = null;
    }

    /** Inserts a value, keeping the tree balanced and the root black. */
    public void insert(T data) {
        if (root == null) {
            root = new Node<>(data);
            root.color = Color.BLACK;
        } else {
            // A rotation scheduled at the top level is impossible (the root never
            // flags a red-red conflict against itself), so it is safely ignored.
            root = insertHelp(root, data).node;
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
     * Recursively inserts {@code data} under {@code node}. On the way back up it
     * applies any rotation the child frame requested, then reports its own
     * (possibly new) subtree root and any rotation the parent frame must apply.
     */
    private InsertResult<T> insertHelp(Node<T> node, T data) {
        if (node == null) {
            return new InsertResult<>(new Node<>(data), RotationCase.NONE);
        }

        boolean redConflict;
        RotationCase childRotation;
        if (data.compareTo(node.data) < 0) {
            InsertResult<T> child = insertHelp(node.left, data);
            node.left = child.node;
            node.left.parent = node;
            childRotation = child.rotation;
            redConflict = node != root && isRed(node) && isRed(node.left);
        } else {
            InsertResult<T> child = insertHelp(node.right, data);
            node.right = child.node;
            node.right.parent = node;
            childRotation = child.rotation;
            redConflict = node != root && isRed(node) && isRed(node.right);
        }

        node = applyRotation(node, childRotation);

        RotationCase scheduled = redConflict ? scheduleRotationFor(node) : RotationCase.NONE;
        return new InsertResult<>(node, scheduled);
    }

    /**
     * Applies the given rotation, if any, to this subtree root, recoloring the
     * rotated nodes as the original algorithm did, and returns the new root.
     */
    private Node<T> applyRotation(Node<T> node, RotationCase rotation) {
        switch (rotation) {
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
        return node;
    }

    /**
     * Resolves a red-red violation at {@code node}. When the uncle is red this
     * recolors locally and returns {@link RotationCase#NONE}; otherwise it
     * returns the rotation the grandparent frame must perform.
     */
    private RotationCase scheduleRotationFor(Node<T> node) {
        Node<T> parent = node.parent;
        if (parent.right == node) {
            Node<T> uncle = parent.left;
            if (isRed(uncle)) {
                recolorWithRedUncle(node, uncle, parent);
            } else if (isRed(node.left)) {
                return RotationCase.RIGHT_LEFT;
            } else if (isRed(node.right)) {
                return RotationCase.LEFT;
            }
        } else {
            Node<T> uncle = parent.right;
            if (isRed(uncle)) {
                recolorWithRedUncle(node, uncle, parent);
            } else if (isRed(node.left)) {
                return RotationCase.RIGHT;
            } else if (isRed(node.right)) {
                return RotationCase.LEFT_RIGHT;
            }
        }
        return RotationCase.NONE;
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
