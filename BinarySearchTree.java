import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class BinarySearchTree<T> extends AbstractCollection<T> {
    private static final class Node<T> {
        private final T data;
        private Node<T> left;
        private Node<T> right;

        private Node(T data) {
            this.data = data;
        }
    }

    private static final class Cursor<T> {
        private final BinarySearchTree<T> tree;
        private Node<T> parent;
        private Node<T> current;
        private boolean rightChild;
        private boolean atRoot = true;

        private Cursor(BinarySearchTree<T> tree) {
            this.tree = tree;
            this.current = tree.root;
        }

        private boolean found() {
            return current != null;
        }

        private Node<T> currentNode() {
            return current;
        }

        private void descend(boolean toRightChild) {
            parent = current;
            current = toRightChild ? current.right : current.left;
            rightChild = toRightChild;
            atRoot = false;
        }

        private void insert(Node<T> newNode) {
            if (atRoot) {
                tree.root = newNode;
                current = newNode;
                return;
            }

            if (rightChild) {
                parent.right = newNode;
            } else {
                parent.left = newNode;
            }

            current = newNode;
        }
    }

    private final Comparator<? super T> comparator;
    private Node<T> root;
    private int size;

    private BinarySearchTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public boolean add(T value) {
        Node<T> newNode = new Node<>(requireValue(value));
        Cursor<T> cursor = locate(newNode.data);

        if (cursor.found()) {
            return false;
        }

        cursor.insert(newNode);
        size++;
        return true;
    }

    public int addEach(Iterable<? extends T> values) {
        Objects.requireNonNull(values, "values");
        int addedCount = 0;

        for (T value : values) {
            if (add(value)) {
                addedCount++;
            }
        }

        return addedCount;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object value) {
        if (value == null) {
            return false;
        }

        try {
            return locate((T) value).found();
        } catch (ClassCastException exception) {
            return false;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator<>(root);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    public static <T extends Comparable<? super T>> BinarySearchTree<T> create() {
        return orderedBy(Comparator.naturalOrder());
    }

    public static <T> BinarySearchTree<T> orderedBy(Comparator<? super T> comparator) {
        return new BinarySearchTree<>(comparator);
    }

    public static <T> BinarySearchTree<T> orderedBy(
        Comparator<? super T> comparator,
        Iterable<? extends T> values
    ) {
        BinarySearchTree<T> tree = orderedBy(comparator);
        tree.addEach(values);
        return tree;
    }

    public static <T extends Comparable<? super T>> BinarySearchTree<T> from(
        Iterable<? extends T> values
    ) {
        return orderedBy(Comparator.naturalOrder(), values);
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> BinarySearchTree<T> fromValues(T... values) {
        return from(Arrays.asList(values));
    }

    private T requireValue(T value) {
        return Objects.requireNonNull(value, "value");
    }

    private int compare(T left, T right) {
        return comparator.compare(left, right);
    }

    private Cursor<T> locate(T value) {
        Cursor<T> cursor = new Cursor<>(this);

        while (cursor.found()) {
            int comparison = compare(value, cursor.currentNode().data);

            if (comparison == 0) {
                return cursor;
            }

            cursor.descend(comparison > 0);
        }

        return cursor;
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
