import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

final class RedBlackTree<T> implements Iterable<T> {
    private enum Color {
        RED,
        BLACK
    }

    private static final class Node<T> {
        final T data;
        Color color;
        Node<T> left;
        Node<T> right;
        Node<T> parent;

        Node(T data) {
            this.data = data;
            this.color = Color.RED;
        }
    }

    private final Comparator<? super T> comparator;
    private Node<T> root;
    private int size;

    RedBlackTree() {
        this(naturalOrderComparator());
    }

    RedBlackTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
        this.root = null;
        this.size = 0;
    }

    public void insert(T data) {
        Objects.requireNonNull(data, "data");

        Node<T> inserted = bstInsert(data);
        fixAfterInsert(inserted);
        size++;
    }

    public boolean contains(T data) {
        Objects.requireNonNull(data, "data");

        Node<T> current = root;
        while (current != null) {
            int comparison = compare(data, current.data);
            if (comparison == 0) {
                return true;
            }

            current = comparison < 0 ? current.left : current.right;
        }

        return false;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    public boolean isValidRedBlackTree() {
        if (root == null) {
            return true;
        }

        if (root.color != Color.BLACK) {
            return false;
        }

        return validateStructure(root, null, false, null, false) != -1;
    }

    private Node<T> bstInsert(T data) {
        Node<T> parent = null;
        Node<T> current = root;

        while (current != null) {
            parent = current;
            int comparison = compare(data, current.data);
            current = comparison < 0 ? current.left : current.right;
        }

        Node<T> inserted = new Node<>(data);
        inserted.parent = parent;

        if (parent == null) {
            root = inserted;
        } else if (compare(data, parent.data) < 0) {
            parent.left = inserted;
        } else {
            parent.right = inserted;
        }

        return inserted;
    }

    private void fixAfterInsert(Node<T> node) {
        while (node != root && colorOf(parentOf(node)) == Color.RED) {
            Node<T> parent = parentOf(node);
            Node<T> grandparent = parentOf(parent);

            node = fixAfterInsert(node, parent, grandparent, isLeftChild(parent));
        }

        if (root != null) {
            root.color = Color.BLACK;
        }
    }

    private Node<T> fixAfterInsert(Node<T> node, Node<T> parent, Node<T> grandparent, boolean parentIsLeftChild) {
        Node<T> uncle = parentIsLeftChild ? grandparent.right : grandparent.left;

        if (colorOf(uncle) == Color.RED) {
            setColor(parent, Color.BLACK);
            setColor(uncle, Color.BLACK);
            setColor(grandparent, Color.RED);
            return grandparent;
        }

        if (parentIsLeftChild) {
            if (node == parent.right) {
                rotateLeft(parent);
                node = parent;
                parent = parentOf(node);
            }

            setColor(parent, Color.BLACK);
            setColor(grandparent, Color.RED);
            rotateRight(grandparent);
            return parent;
        }

        if (node == parent.left) {
            rotateRight(parent);
            node = parent;
            parent = parentOf(node);
        }

        setColor(parent, Color.BLACK);
        setColor(grandparent, Color.RED);
        rotateLeft(grandparent);
        return parent;
    }

    private void rotateLeft(Node<T> node) {
        if (node == null || node.right == null) {
            throw new IllegalStateException("rotateLeft requires a right child");
        }

        Node<T> pivot = node.right;
        node.right = pivot.left;

        if (pivot.left != null) {
            pivot.left.parent = node;
        }

        pivot.parent = node.parent;
        if (node.parent == null) {
            root = pivot;
        } else if (node == node.parent.left) {
            node.parent.left = pivot;
        } else {
            node.parent.right = pivot;
        }

        pivot.left = node;
        node.parent = pivot;
    }

    private void rotateRight(Node<T> node) {
        if (node == null || node.left == null) {
            throw new IllegalStateException("rotateRight requires a left child");
        }

        Node<T> pivot = node.left;
        node.left = pivot.right;

        if (pivot.right != null) {
            pivot.right.parent = node;
        }

        pivot.parent = node.parent;
        if (node.parent == null) {
            root = pivot;
        } else if (node == node.parent.right) {
            node.parent.right = pivot;
        } else {
            node.parent.left = pivot;
        }

        pivot.right = node;
        node.parent = pivot;
    }

    private int compare(T left, T right) {
        return comparator.compare(left, right);
    }

    @SuppressWarnings("unchecked")
    private static <T> Comparator<? super T> naturalOrderComparator() {
        return (left, right) -> ((Comparable<? super T>) left).compareTo(right);
    }

    private static <T> Color colorOf(Node<T> node) {
        return node == null ? Color.BLACK : node.color;
    }

    private static <T> void setColor(Node<T> node, Color color) {
        if (node != null) {
            node.color = color;
        }
    }

    private static <T> Node<T> parentOf(Node<T> node) {
        return node == null ? null : node.parent;
    }

    private static <T> boolean isLeftChild(Node<T> node) {
        return node != null && node.parent != null && node.parent.left == node;
    }

    private int validateStructure(Node<T> node, T lowerBound, boolean lowerInclusive, T upperBound, boolean upperInclusive) {
        if (node == null) {
            return 1;
        }

        if (lowerBound != null) {
            int comparison = compare(node.data, lowerBound);
            if (comparison < 0 || (!lowerInclusive && comparison == 0)) {
                return -1;
            }
        }

        if (upperBound != null) {
            int comparison = compare(node.data, upperBound);
            if (comparison > 0 || (!upperInclusive && comparison == 0)) {
                return -1;
            }
        }

        if (node.color == Color.RED) {
            if (colorOf(node.left) == Color.RED || colorOf(node.right) == Color.RED) {
                return -1;
            }
        }

        if (node.left != null && node.left.parent != node) {
            return -1;
        }
        if (node.right != null && node.right.parent != node) {
            return -1;
        }

        int leftBlackHeight = validateStructure(node.left, lowerBound, lowerInclusive, node.data, false);
        if (leftBlackHeight == -1) {
            return -1;
        }

        int rightBlackHeight = validateStructure(node.right, node.data, true, upperBound, upperInclusive);
        if (rightBlackHeight == -1 || leftBlackHeight != rightBlackHeight) {
            return -1;
        }

        return leftBlackHeight + (node.color == Color.BLACK ? 1 : 0);
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator(root);
    }

    @Override
    public String toString() {
        return treeString();
    }

    public String inorderString() {
        StringBuilder builder = new StringBuilder();
        inorderString(root, builder);
        return builder.toString();
    }

    private void inorderString(Node<T> node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        inorderString(node.left, builder);
        builder.append(node.data).append(' ');
        inorderString(node.right, builder);
    }

    public String treeString() {
        StringBuilder builder = new StringBuilder();
        treeString(root, 0, builder);
        return builder.toString();
    }

    private void treeString(Node<T> node, int indent, StringBuilder builder) {
        if (node == null) {
            return;
        }

        int nextIndent = indent + 10;
        treeString(node.right, nextIndent, builder);

        builder.append('\n');
        for (int i = 10; i < nextIndent; i++) {
            builder.append(' ');
        }

        builder.append(node.data)
            .append('(')
            .append(node.color == Color.RED ? 'R' : 'B')
            .append(')')
            .append('\n');
        treeString(node.left, nextIndent, builder);
    }

    private final class InOrderIterator implements Iterator<T> {
        private final Deque<Node<T>> stack = new ArrayDeque<>();

        InOrderIterator(Node<T> root) {
            pushLeftBranch(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }

            Node<T> node = stack.pop();
            pushLeftBranch(node.right);
            return node.data;
        }

        private void pushLeftBranch(Node<T> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }
    }
}

final class RedBlackTreeDemo {
    private RedBlackTreeDemo() {
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            if (!tree.isValidRedBlackTree()) {
                throw new IllegalStateException("red-black tree invariant violation");
            }
            System.out.println();
            for (int current : tree) {
                System.out.print(current);
                System.out.print(' ');
            }
        }

        System.out.print(tree.treeString());
    }
}
