import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AVLTree<T extends Comparable<? super T>> {
    private Node<T> root;
    private int size;

    private static final class Node<T> {
        private final T value;
        private Node<T> left;
        private Node<T> right;
        private int height;

        private Node(T value) {
            this.value = value;
            this.height = 1;
        }
    }

    private static final class InsertResult<T> {
        private final Node<T> node;
        private final boolean inserted;

        private InsertResult(Node<T> node, boolean inserted) {
            this.node = node;
            this.inserted = inserted;
        }
    }

    public boolean insert(T value) {
        Objects.requireNonNull(value, "value");

        InsertResult<T> result = insert(root, value);
        root = result.node;

        if (result.inserted) {
            size++;
        }

        return result.inserted;
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value, "value");

        Node<T> current = root;
        while (current != null) {
            int comparison = value.compareTo(current.value);
            if (comparison == 0) {
                return true;
            }

            current = comparison < 0 ? current.left : current.right;
        }

        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public List<T> preOrderTraversal() {
        List<T> values = new ArrayList<>();
        buildPreOrderTraversal(root, values);
        return values;
    }

    public String preOrderString() {
        StringBuilder traversal = new StringBuilder();

        for (T value : preOrderTraversal()) {
            if (traversal.length() > 0) {
                traversal.append(' ');
            }
            traversal.append(value);
        }

        return traversal.toString();
    }

    private InsertResult<T> insert(Node<T> node, T value) {
        if (node == null) {
            return new InsertResult<>(new Node<>(value), true);
        }

        int comparison = value.compareTo(node.value);
        if (comparison < 0) {
            InsertResult<T> result = insert(node.left, value);
            node.left = result.node;
            return finishInsertion(node, result.inserted);
        }

        if (comparison > 0) {
            InsertResult<T> result = insert(node.right, value);
            node.right = result.node;
            return finishInsertion(node, result.inserted);
        }

        return new InsertResult<>(node, false);
    }

    private InsertResult<T> finishInsertion(Node<T> node, boolean inserted) {
        if (!inserted) {
            return new InsertResult<>(node, false);
        }

        updateHeight(node);
        return new InsertResult<>(rebalance(node), true);
    }

    private Node<T> rebalance(Node<T> node) {
        int balanceFactor = getBalanceFactor(node);

        if (balanceFactor > 1) {
            if (getBalanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (balanceFactor < -1) {
            if (getBalanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private void buildPreOrderTraversal(Node<T> node, List<T> values) {
        if (node == null) {
            return;
        }

        values.add(node.value);
        buildPreOrderTraversal(node.left, values);
        buildPreOrderTraversal(node.right, values);
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> newRoot = node.left;
        Node<T> transferredSubtree = newRoot.right;

        newRoot.right = node;
        node.left = transferredSubtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> newRoot = node.right;
        Node<T> transferredSubtree = newRoot.left;

        newRoot.left = node;
        node.right = transferredSubtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int getBalanceFactor(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }
}
