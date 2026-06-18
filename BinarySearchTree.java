import java.util.ArrayDeque;
import java.util.Arrays;
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

    private enum Branch {
        ROOT,
        LEFT,
        RIGHT
    }

    private static final class Location<T> {
        private final Node<T> node;
        private final Node<T> parent;
        private final Branch branch;

        private Location(Node<T> node, Node<T> parent, Branch branch) {
            this.node = node;
            this.parent = parent;
            this.branch = branch;
        }

        private boolean found() {
            return node != null;
        }

        private void insertInto(BinarySearchTree<T> tree, Node<T> newNode) {
            if (branch == Branch.ROOT) {
                tree.root = newNode;
                return;
            }

            if (branch == Branch.LEFT) {
                parent.left = newNode;
                return;
            }

            parent.right = newNode;
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
        Location<T> location = locate(newNode.data);

        if (location.found()) {
            return false;
        }

        location.insertInto(this, newNode);
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

    public static <T> BinarySearchTree<T> orderedBy(
        Comparator<? super T> comparator,
        Iterable<? extends T> values
    ) {
        BinarySearchTree<T> tree = orderedBy(comparator);
        tree.addAll(values);
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

    private Location<T> locate(T value) {
        Node<T> current = root;
        Node<T> parent = null;
        Branch branch = Branch.ROOT;

        while (current != null) {
            int comparison = compare(value, current.data);

            if (comparison == 0) {
                return new Location<>(current, parent, branch);
            }

            parent = current;
            branch = comparison > 0 ? Branch.RIGHT : Branch.LEFT;
            current = comparison > 0 ? current.right : current.left;
        }

        return new Location<>(null, parent, branch);
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
