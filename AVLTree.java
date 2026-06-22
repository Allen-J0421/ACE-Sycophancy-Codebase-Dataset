import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
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
 * <p>Keys are ordered either by their {@linkplain Comparable natural ordering}
 * or by a {@link Comparator} supplied at construction, mirroring
 * {@link java.util.TreeMap}. When no comparator is given, keys must implement
 * {@code Comparable}; otherwise insertion throws {@link ClassCastException}.
 * The tree stores at most one node per distinct key (duplicates, as judged by
 * the active ordering, are ignored). It is not thread-safe; external
 * synchronization is required for concurrent use.
 *
 * @param <T> the type of keys stored in the tree
 */
public class AVLTree<T> implements Iterable<T> {

    /**
     * An internal tree node. This is an implementation detail and is never
     * exposed through the public API, so its fields can stay package-light.
     */
    private static final class Node<T> {
        T key; // overwritten during two-child deletion (successor copy)
        Node<T> left;
        Node<T> right;
        int height;

        Node(T key) {
            this.key = key;
            this.height = 1;
        }
    }

    /** Ordering supplied at construction, or {@code null} for natural ordering. */
    private final Comparator<? super T> comparator;

    private Node<T> root;
    private int size;

    /**
     * Creates a tree that orders keys by their natural ordering. Keys must
     * implement {@link Comparable}.
     */
    public AVLTree() {
        this.comparator = null;
    }

    /**
     * Creates a tree that orders keys using the given comparator. This allows
     * custom orderings and keys that do not implement {@link Comparable}.
     *
     * @param comparator the ordering to use; must not be {@code null} (use the
     *                    no-arg constructor for natural ordering)
     */
    public AVLTree(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new NullPointerException(
                    "comparator must not be null; use AVLTree() for natural ordering");
        }
        this.comparator = comparator;
    }

    /** Returns the comparator used to order keys, or {@code null} for natural ordering. */
    public Comparator<? super T> comparator() {
        return comparator;
    }

    /** Inserts {@code key} into the tree. Duplicate keys are ignored. */
    public void insert(T key) {
        if (key == null) {
            throw new NullPointerException("AVL tree does not permit null keys");
        }
        root = insert(root, key);
    }

    /**
     * Removes {@code key} from the tree, re-balancing as needed.
     *
     * @return {@code true} if the key was present and removed, {@code false} if
     *         it was not in the tree
     */
    public boolean remove(T key) {
        if (key == null) {
            throw new NullPointerException("AVL tree does not permit null keys");
        }
        int sizeBefore = size;
        root = remove(root, key);
        return size != sizeBefore;
    }

    /** Returns {@code true} if {@code key} is present in the tree. */
    public boolean contains(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = compare(key, current.key);
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

    /**
     * Returns a multi-line, indented diagram of the tree's structure, with each
     * child labelled {@code L:} or {@code R:} so left-only and right-only
     * children are distinguishable. An empty tree renders as {@code (empty)}.
     *
     * <p>Example for keys inserted as 10, 20, 30, 40, 50, 25:
     * <pre>
     * 30
     * ├── L: 20
     * │   ├── L: 10
     * │   └── R: 25
     * └── R: 40
     *     └── R: 50
     * </pre>
     *
     * @return a human-readable rendering of the tree hierarchy (no trailing newline)
     */
    public String toTreeString() {
        if (root == null) {
            return "(empty)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(root.key).append('\n');
        appendChildren(root, "", sb);
        // Drop the trailing newline so the result reads like a single value.
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private void appendChildren(Node<T> node, String prefix, StringBuilder sb) {
        if (node.left != null) {
            appendChild(node.left, "L", prefix, node.right == null, sb);
        }
        if (node.right != null) {
            appendChild(node.right, "R", prefix, true, sb);
        }
    }

    private void appendChild(Node<T> node, String label, String prefix, boolean last,
                             StringBuilder sb) {
        sb.append(prefix)
          .append(last ? "└── " : "├── ")
          .append(label).append(": ").append(node.key).append('\n');
        appendChildren(node, prefix + (last ? "    " : "│   "), sb);
    }

    // --- Internal recursion and balancing -------------------------------

    /**
     * Compares two keys using the configured comparator, or their natural
     * ordering when none was supplied.
     *
     * @throws ClassCastException if natural ordering is in effect and the keys
     *                            are not mutually comparable
     */
    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super T>) a).compareTo(b);
    }

    private Node<T> insert(Node<T> node, T key) {
        if (node == null) {
            size++;
            return new Node<>(key);
        }

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
        } else {
            return node; // duplicate key: no change
        }

        updateHeight(node);
        return rebalance(node);
    }

    private Node<T> remove(Node<T> node, T key) {
        if (node == null) {
            return null; // key not found
        }

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else if (node.left == null || node.right == null) {
            // Zero or one child: splice the node out by promoting its child.
            size--;
            node = (node.left != null) ? node.left : node.right;
        } else {
            // Two children: replace with the in-order successor, then remove
            // that successor (a node with at most one child) from the right.
            Node<T> successor = min(node.right);
            node.key = successor.key;
            node.right = remove(node.right, successor.key);
        }

        if (node == null) {
            return null; // the removed node was a leaf
        }
        updateHeight(node);
        return rebalance(node);
    }

    /** Returns the left-most (smallest) node of the given subtree. */
    private static <T> Node<T> min(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Restores the AVL invariant at {@code node} after a single insertion or
     * removal. The rotation case is chosen from the balance factors of the
     * heavy child, which works for both operations (an inserted/removed key is
     * not needed).
     */
    private Node<T> rebalance(Node<T> node) {
        int balance = balanceFactor(node);

        // Left-heavy
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = leftRotate(node.left); // Left-Right case
            }
            return rightRotate(node);
        }

        // Right-heavy
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
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
