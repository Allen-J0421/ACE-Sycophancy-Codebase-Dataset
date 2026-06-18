import java.util.Comparator;

/**
 * A generic binary search tree supporting insertion and membership queries.
 *
 * <p>Ordering is determined either by a {@link Comparator} supplied at
 * construction, or—if none is given—by the natural ordering of the values
 * ({@link Comparable}). Duplicate values (those that compare equal) are ignored
 * on insertion, so the tree models a set. Searching runs in O(h) time, where
 * {@code h} is the height of the tree.
 *
 * <p>When relying on natural ordering, values must implement {@link Comparable};
 * otherwise a {@link ClassCastException} is thrown when they are first compared.
 *
 * @param <T> the type of values held in the tree
 */
class BinarySearchTree<T> {

    /** A single tree node holding a value and links to its two subtrees. */
    private static final class Node<T> {
        final T value;
        Node<T> left, right;

        Node(T value) {
            this.value = value;
        }
    }

    /** Ordering to use, or {@code null} to use the values' natural ordering. */
    private final Comparator<? super T> comparator;

    private Node<T> root;

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
     * Inserts a value into the tree. Values that compare equal to one already
     * present are ignored.
     *
     * @param value the value to insert; must not be {@code null}
     */
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (root == null) {
            root = new Node<>(value);
            return;
        }
        Node<T> current = root;
        while (true) {
            int cmp = compare(value, current.value);
            if (cmp == 0) {
                return; // already present
            } else if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    return;
                }
                current = current.right;
            }
        }
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

    public static void main(String[] args) {
        // Natural ordering.
        BinarySearchTree<Integer> numbers = new BinarySearchTree<>();
        for (int value : new int[] {6, 2, 8, 7, 9}) {
            numbers.insert(value);
        }
        System.out.println(numbers.contains(7));

        // Custom ordering: case-insensitive strings.
        BinarySearchTree<String> names =
                new BinarySearchTree<>(String.CASE_INSENSITIVE_ORDER);
        names.insert("Bravo");
        names.insert("alpha");
        System.out.println(names.contains("ALPHA"));
    }
}
