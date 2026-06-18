/**
 * A generic binary search tree supporting insertion and membership queries.
 *
 * <p>Values are ordered by their natural ordering ({@link Comparable}). Duplicate
 * values are ignored on insertion, so the tree models a set. Searching runs in
 * O(h) time, where {@code h} is the height of the tree.
 *
 * @param <T> the type of values held in the tree
 */
class BinarySearchTree<T extends Comparable<T>> {

    /** A single tree node holding a value and links to its two subtrees. */
    private static final class Node<T> {
        final T value;
        Node<T> left, right;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;

    /**
     * Inserts a value into the tree. Values already present are ignored.
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
            int cmp = value.compareTo(current.value);
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
     * @param key the value to search for
     * @return {@code true} if {@code key} is in the tree, {@code false} otherwise
     */
    public boolean contains(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.value);
            if (cmp == 0) {
                return true;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        for (int value : new int[] {6, 2, 8, 7, 9}) {
            tree.insert(value);
        }

        int key = 7;
        System.out.println(tree.contains(key));
    }
}
