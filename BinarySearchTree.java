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

    private interface NodeSlot<T> {
        Node<T> get();

        void set(Node<T> node);
    }

    private static final class RootSlot<T> implements NodeSlot<T> {
        private final BinarySearchTree<T> tree;

        private RootSlot(BinarySearchTree<T> tree) {
            this.tree = tree;
        }

        @Override
        public Node<T> get() {
            return tree.root;
        }

        @Override
        public void set(Node<T> node) {
            tree.root = node;
        }
    }

    private static final class ChildSlot<T> implements NodeSlot<T> {
        private final Node<T> parent;
        private final boolean rightChild;

        private ChildSlot(Node<T> parent, boolean rightChild) {
            this.parent = parent;
            this.rightChild = rightChild;
        }

        @Override
        public Node<T> get() {
            return rightChild ? parent.right : parent.left;
        }

        @Override
        public void set(Node<T> node) {
            if (rightChild) {
                parent.right = node;
                return;
            }

            parent.left = node;
        }
    }

    private static final class Location<T> {
        private final Node<T> node;
        private final NodeSlot<T> slot;

        private Location(Node<T> node, NodeSlot<T> slot) {
            this.node = node;
            this.slot = slot;
        }

        private boolean found() {
            return node != null;
        }

        private void insertInto(Node<T> newNode) {
            slot.set(newNode);
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

        location.insertInto(newNode);
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
        NodeSlot<T> slot = new RootSlot<>(this);
        Node<T> current = slot.get();

        while (current != null) {
            int comparison = compare(value, current.data);

            if (comparison == 0) {
                return new Location<>(current, slot);
            }

            slot = new ChildSlot<>(current, comparison > 0);
            current = slot.get();
        }

        return new Location<>(null, slot);
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
