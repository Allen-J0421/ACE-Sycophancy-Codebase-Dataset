import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class BinarySearchTree<T> implements Iterable<T> {
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
    private int size;

    public BinarySearchTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public boolean add(T value) {
        Node<T> newNode = new Node<>(requireValue(value));

        if (root == null) {
            root = newNode;
            size = 1;
            return true;
        }

        Node<T> current = root;

        while (true) {
            int comparison = compare(newNode.data, current.data);

            if (comparison < 0) {
                if (current.left == null) {
                    current.left = newNode;
                    size++;
                    return true;
                }

                current = current.left;
            } else if (comparison > 0) {
                if (current.right == null) {
                    current.right = newNode;
                    size++;
                    return true;
                }

                current = current.right;
            } else {
                return false;
            }
        }
    }

    public int addAll(Iterable<? extends T> values) {
        Objects.requireNonNull(values, "values");
        int addedCount = 0;

        for (T value : values) {
            if (add(value)) {
                addedCount++;
            }
        }

        return addedCount;
    }

    public boolean contains(T value) {
        return findNode(requireValue(value)) != null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator<>(root);
    }

    public static <T extends Comparable<? super T>> BinarySearchTree<T> create() {
        return new BinarySearchTree<>(Comparator.naturalOrder());
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> BinarySearchTree<T> fromValues(T... values) {
        BinarySearchTree<T> tree = create();

        for (T value : values) {
            tree.add(value);
        }

        return tree;
    }

    private T requireValue(T value) {
        return Objects.requireNonNull(value, "value");
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

    private static final class InOrderIterator<T> implements Iterator<T> {
        private final Deque<Node<T>> stack = new ArrayDeque<>();

        private InOrderIterator(Node<T> root) {
            pushLeftBranch(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the tree");
            }

            Node<T> next = stack.pop();
            pushLeftBranch(next.right);
            return next.data;
        }

        private void pushLeftBranch(Node<T> node) {
            Node<T> current = node;

            while (current != null) {
                stack.push(current);
                current = current.left;
            }
        }
    }
}
