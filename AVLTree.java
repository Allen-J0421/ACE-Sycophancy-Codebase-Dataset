import java.util.ArrayList;
import java.util.List;

/**
 * A self-balancing binary search tree (AVL tree).
 *
 * <p>The tree maintains the AVL invariant: for every node, the heights of its
 * left and right subtrees differ by at most one. This guarantees O(log n)
 * insertion and lookup.
 *
 * <p>The tree is generic over any {@link Comparable} key type and stores at most
 * one node per distinct key (duplicate insertions are ignored). It is not
 * thread-safe; external synchronization is required for concurrent use.
 *
 * @param <T> the type of keys stored in the tree
 */
public class AVLTree<T extends Comparable<T>> {

    /**
     * An internal tree node. This is an implementation detail and is never
     * exposed through the public API, so its fields can stay package-light.
     */
    private static final class Node<T> {
        final T key;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T key) {
            this.key = key;
            this.height = 1;
        }
    }

    private Node<T> root;
    private int size;

    /** Inserts {@code key} into the tree. Duplicate keys are ignored. */
    public void insert(T key) {
        if (key == null) {
            throw new NullPointerException("AVL tree does not permit null keys");
        }
        root = insert(root, key);
    }

    /** Returns {@code true} if {@code key} is present in the tree. */
    public boolean contains(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return true;
            }
        }
        return false;
    }

    /** Returns the number of keys stored in the tree. */
    public int size() {
        return size;
    }

    /** Returns {@code true} if the tree contains no keys. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the height of the tree (0 for an empty tree). */
    public int height() {
        return height(root);
    }

    /** Returns the keys in pre-order (root, left, right). */
    public List<T> preOrder() {
        List<T> out = new ArrayList<>(size);
        preOrder(root, out);
        return out;
    }

    /** Returns the keys in ascending order (in-order traversal). */
    public List<T> inOrder() {
        List<T> out = new ArrayList<>(size);
        inOrder(root, out);
        return out;
    }

    // --- Internal recursion and balancing -------------------------------

    private Node<T> insert(Node<T> node, T key) {
        if (node == null) {
            size++;
            return new Node<>(key);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
        } else {
            return node; // duplicate key: no change
        }

        updateHeight(node);
        return rebalance(node, key);
    }

    /** Restores the AVL invariant at {@code node} after an insertion. */
    private Node<T> rebalance(Node<T> node, T insertedKey) {
        int balance = balanceFactor(node);

        // Left-heavy
        if (balance > 1) {
            if (insertedKey.compareTo(node.left.key) > 0) {
                node.left = leftRotate(node.left); // Left-Right case
            }
            return rightRotate(node);
        }

        // Right-heavy
        if (balance < -1) {
            if (insertedKey.compareTo(node.right.key) < 0) {
                node.right = rightRotate(node.right); // Right-Left case
            }
            return leftRotate(node);
        }

        return node;
    }

    private Node<T> rightRotate(Node<T> y) {
        Node<T> x = y.left;
        Node<T> t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node<T> leftRotate(Node<T> x) {
        Node<T> y = x.right;
        Node<T> t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private static int height(Node<?> node) {
        return node == null ? 0 : node.height;
    }

    private static void updateHeight(Node<?> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private static int balanceFactor(Node<?> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void preOrder(Node<T> node, List<T> out) {
        if (node != null) {
            out.add(node.key);
            preOrder(node.left, out);
            preOrder(node.right, out);
        }
    }

    private void inOrder(Node<T> node, List<T> out) {
        if (node != null) {
            inOrder(node.left, out);
            out.add(node.key);
            inOrder(node.right, out);
        }
    }
}
