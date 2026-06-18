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

    private static final class SearchResult<T> {
        private final Node<T> node;
        private final Node<T> parent;
        private final int comparison;

        private SearchResult(Node<T> node, Node<T> parent, int comparison) {
            this.node = node;
            this.parent = parent;
            this.comparison = comparison;
        }

        private boolean found() {
            return node != null;
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
        SearchResult<T> searchResult = locate(newNode.data);

        if (searchResult.found()) {
            return false;
        }

        attach(searchResult.parent, searchResult.comparison, newNode);
        size++;
        return true;
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
        return locate(requireValue(value)).found();
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
        return orderedBy(Comparator.naturalOrder());
    }

    public static <T> BinarySearchTree<T> orderedBy(Comparator<? super T> comparator) {
        return new BinarySearchTree<>(comparator);
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

    private void attach(Node<T> parent, int comparison, Node<T> newNode) {
        if (parent == null) {
            root = newNode;
            return;
        }

        if (comparison < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
    }

    private SearchResult<T> locate(T value) {
        Node<T> current = root;
        Node<T> parent = null;
        int comparison = 0;

        while (current != null) {
            comparison = compare(value, current.data);

            if (comparison == 0) {
                return new SearchResult<>(current, parent, comparison);
            }

            parent = current;
            current = comparison > 0 ? current.right : current.left;
        }

        return new SearchResult<>(null, parent, comparison);
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
