import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A self-balancing (AVL) binary search tree supporting insertion, membership
 * queries, and in-order iteration.
 *
 * <p>After every insertion the tree restores the AVL invariant—the heights of
 * any node's two subtrees differ by at most one—via rotations. This keeps the
 * height in O(log n), so both {@link #insert} and {@link #contains} run in
 * guaranteed O(log n) time even when values arrive in sorted order (the case
 * that degrades an unbalanced tree to a linked list).
 *
 * <p>The tree is {@link Iterable}: iterating yields values in ascending order
 * under the tree's ordering. Iteration is lazy, using O(h) auxiliary space.
 *
 * <p>Ordering is determined either by a {@link Comparator} supplied at
 * construction, or—if none is given—by the natural ordering of the values
 * ({@link Comparable}). Duplicate values (those that compare equal) are ignored
 * on insertion, so the tree models a set.
 *
 * <p>When relying on natural ordering, values must implement {@link Comparable};
 * otherwise a {@link ClassCastException} is thrown when they are first compared.
 *
 * @param <T> the type of values held in the tree
 */
class BinarySearchTree<T> implements Iterable<T> {

    /** A single tree node holding a value, subtree links, and its height. */
    private static final class Node<T> {
        final T value;
        Node<T> left, right;
        int height = 1; // height of a leaf is 1

        Node(T value) {
            this.value = value;
        }
    }

    /** Ordering to use, or {@code null} to use the values' natural ordering. */
    private final Comparator<? super T> comparator;

    private Node<T> root;

    /** Number of structural modifications, used to detect concurrent
     *  modification during iteration. */
    private int modCount;

    /** Set by {@link #insert(Node, Object)} when a new node is actually added,
     *  so duplicate (no-op) inserts do not bump {@link #modCount}. */
    private boolean modified;

    /** Creates a tree ordered by the natural ordering of its values. */
    public BinarySearchTree() {
        this.comparator = null;
    }

    /**
     * Creates a tree ordered by the given comparator.
     *
     * @param comparator the ordering to use; must not be {@code null}
     */
    public BinarySearchTree(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * Compares two values using the configured ordering. Falls back to natural
     * ordering when no comparator was supplied.
     */
    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        return comparator != null
                ? comparator.compare(a, b)
                : ((Comparable<? super T>) a).compareTo(b);
    }

    /**
     * Inserts a value into the tree, rebalancing as needed. Values that compare
     * equal to one already present are ignored.
     *
     * @param value the value to insert; must not be {@code null}
     */
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        modified = false;
        root = insert(root, value);
        if (modified) {
            modCount++;
        }
    }

    /** Inserts {@code value} into the subtree rooted at {@code node}, returning
     *  the new (rebalanced) subtree root. */
    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            modified = true;
            return new Node<>(value);
        }
        int cmp = compare(value, node.value);
        if (cmp == 0) {
            return node; // already present
        } else if (cmp < 0) {
            node.left = insert(node.left, value);
        } else {
            node.right = insert(node.right, value);
        }
        updateHeight(node);
        return rebalance(node);
    }

    /**
     * Returns whether the given value is present in the tree.
     *
     * @param key the value to search for; a {@code null} key is never present
     * @return {@code true} if {@code key} is in the tree, {@code false} otherwise
     */
    public boolean contains(T key) {
        if (key == null) {
            return false;
        }
        Node<T> current = root;
        while (current != null) {
            int cmp = compare(key, current.value);
            if (cmp == 0) {
                return true;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    /**
     * Returns an iterator over the values in ascending order (an in-order
     * traversal). The iterator is lazy and does not support removal.
     *
     * <p>The iterator is <em>fail-fast</em>: if the tree is structurally
     * modified after the iterator is created, {@link #next()} throws a
     * {@link ConcurrentModificationException} rather than risking arbitrary
     * behavior. This is a best-effort safeguard, not a guarantee.
     */
    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator();
    }

    /** Lazy in-order iterator: the stack holds the path of nodes yet to be
     *  visited, so it uses space proportional to the tree height. */
    private final class InOrderIterator implements Iterator<T> {
        private final Deque<Node<T>> stack = new ArrayDeque<>();
        private int expectedModCount = modCount;

        InOrderIterator() {
            pushLeftSpine(root);
        }

        /** Pushes {@code node} and all of its left descendants. */
        private void pushLeftSpine(Node<T> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            checkForComodification();
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }
            Node<T> node = stack.pop();
            pushLeftSpine(node.right);
            return node.value;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    // --- AVL balancing -----------------------------------------------------

    private static int height(Node<?> node) {
        return node == null ? 0 : node.height;
    }

    private static void updateHeight(Node<?> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    /** Left-subtree height minus right-subtree height. */
    private static int balanceFactor(Node<?> node) {
        return height(node.left) - height(node.right);
    }

    /** Restores the AVL invariant at {@code node}, applying the rotation(s) that
     *  match its imbalance, and returns the subtree's new root. */
    private static <T> Node<T> rebalance(Node<T> node) {
        int balance = balanceFactor(node);
        if (balance > 1) { // left heavy
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left); // left-right case
            }
            return rotateRight(node); // left-left case
        }
        if (balance < -1) { // right heavy
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // right-left case
            }
            return rotateLeft(node); // right-right case
        }
        return node;
    }

    private static <T> Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        y.left = x.right;
        x.right = y;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private static <T> Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        y.left = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    public static void main(String[] args) {
        // Ascending input is the worst case for an unbalanced BST (it would
        // degrade to a height-7 chain); the AVL tree keeps it balanced.
        BinarySearchTree<Integer> numbers = new BinarySearchTree<>();
        for (int value = 1; value <= 7; value++) {
            numbers.insert(value);
        }
        System.out.println(numbers.contains(7));   // true
        System.out.println(numbers.root.height);   // 3 (balanced), not 7

        // In-order iteration yields ascending order.
        StringBuilder ordered = new StringBuilder();
        for (int value : numbers) {
            ordered.append(value).append(' ');
        }
        System.out.println(ordered.toString().trim()); // 1 2 3 4 5 6 7

        // Iteration follows the tree's ordering (here, case-insensitive).
        BinarySearchTree<String> names =
                new BinarySearchTree<>(String.CASE_INSENSITIVE_ORDER);
        names.insert("Bravo");
        names.insert("alpha");
        System.out.println(String.join(", ", names)); // alpha, Bravo

        // Fail-fast: structural modification during iteration is detected.
        try {
            for (int value : numbers) {
                numbers.insert(value + 100); // structural change mid-iteration
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("fail-fast detected");
        }
    }
}
