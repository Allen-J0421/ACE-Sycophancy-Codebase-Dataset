import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
public class AVLTree<T extends Comparable<? super T>> implements Iterable<T> {

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

    /**
     * Returns an iterator over the keys in ascending order.
     *
     * <p>The traversal is lazy: nodes are visited on demand using an explicit
     * stack, so iteration uses O(h) memory (h = tree height) rather than
     * materializing all n keys up front. The iterator is fail-fast — modifying
     * the tree during iteration leaves it in an undefined state.
     */
    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator<>(root);
    }

    /** Returns the keys in ascending order, e.g. {@code [10, 20, 30]}. */
    @Override
    public String toString() {
        return inOrder().toString();
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

    /**
     * Lazy in-order iterator. The stack holds the path of left-descendants
     * still waiting to be yielded, so at any moment it contains at most one
     * node per level of the tree — O(h) memory.
     */
    private static final class InOrderIterator<T> implements Iterator<T> {
        private final Deque<Node<T>> stack = new ArrayDeque<>();

        InOrderIterator(Node<T> root) {
            pushLeftSpine(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }
            Node<T> node = stack.pop();
            // The node's left subtree is already consumed; queue up the keys
            // that come after it by descending the left spine of its right child.
            pushLeftSpine(node.right);
            return node.key;
        }

        private void pushLeftSpine(Node<T> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }
    }
}
