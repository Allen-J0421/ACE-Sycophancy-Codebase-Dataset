import java.util.Objects;

public final class BinarySearchTree<T extends Comparable<? super T>> {
    private static final Integer SEARCH_KEY = 7;
    private static final Integer[] SAMPLE_VALUES = {6, 2, 8, 7, 9};

    private Node<T> root;

    @SafeVarargs
    public static <T extends Comparable<? super T>> BinarySearchTree<T> fromValues(T... values) {
        Objects.requireNonNull(values, "values");

        BinarySearchTree<T> tree = new BinarySearchTree<>();

        for (T value : values) {
            tree.insert(value);
        }

        return tree;
    }

    public BinarySearchTree<T> insert(T value) {
        Objects.requireNonNull(value, "value");

        if (root == null) {
            root = new Node<>(value);
            return this;
        }

        Node<T> current = root;

        while (true) {
            int comparison = value.compareTo(current.value);

            if (comparison == 0) {
                return this;
            }

            if (comparison < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    return this;
                }

                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    return this;
                }

                current = current.right;
            }
        }
    }

    @SafeVarargs
    public final BinarySearchTree<T> insertAll(T... values) {
        Objects.requireNonNull(values, "values");

        for (T value : values) {
            insert(value);
        }

        return this;
    }

    public boolean contains(T key) {
        Objects.requireNonNull(key, "key");
        return contains(root, key);
    }

    private static <T extends Comparable<? super T>> boolean contains(Node<T> root, T key) {
        Node<T> current = root;

        while (current != null) {
            int comparison = key.compareTo(current.value);

            if (comparison == 0) {
                return true;
            }

            current = comparison > 0 ? current.right : current.left;
        }

        return false;
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = BinarySearchTree.fromValues(SAMPLE_VALUES);

        System.out.println(tree.contains(SEARCH_KEY));
    }

    private static final class Node<T> {
        private final T value;
        private Node<T> left;
        private Node<T> right;

        private Node(T value) {
            this.value = value;
        }
    }
}
