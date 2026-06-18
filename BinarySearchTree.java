import java.util.Comparator;
import java.util.Objects;

public final class BinarySearchTree<T> {
    private static final class Node<T> {
        private final T data;
        private Node<T> left;
        private Node<T> right;

        private Node(T data) {
            this.data = data;
        }
    }

    private final Comparator<? super T> comparator;
    private Node<T> root;

    public BinarySearchTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public void insert(T value) {
        Objects.requireNonNull(value, "value");

        if (root == null) {
            root = new Node<>(value);
            return;
        }

        Node<T> current = root;

        while (true) {
            int comparison = compare(value, current.data);

            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    return;
                }

                current = current.left;
            } else if (comparison > 0) {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    return;
                }

                current = current.right;
            } else {
                return;
            }
        }
    }

    public void insertAll(Iterable<? extends T> values) {
        Objects.requireNonNull(values, "values");

        for (T value : values) {
            insert(value);
        }
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value, "value");
        return findNode(value) != null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> BinarySearchTree<T> fromValues(T... values) {
        BinarySearchTree<T> tree = new BinarySearchTree<>(Comparator.naturalOrder());

        for (T value : values) {
            tree.insert(value);
        }

        return tree;
    }

    private int compare(T left, T right) {
        return comparator.compare(left, right);
    }

    private Node<T> findNode(T value) {
        Node<T> current = root;

        while (current != null) {
            int comparison = compare(value, current.data);

            if (comparison == 0) {
                return current;
            }

            current = comparison > 0 ? current.right : current.left;
        }

        return null;
    }
}
