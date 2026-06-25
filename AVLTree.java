import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class AVLTree<T> {
    public enum TraversalOrder {
        PRE_ORDER {
            @Override
            <T> void traverse(Node<T> node, Consumer<? super T> consumer) {
                if (node == null) {
                    return;
                }

                consumer.accept(node.value);
                traverse(node.left, consumer);
                traverse(node.right, consumer);
            }
        },
        IN_ORDER {
            @Override
            <T> void traverse(Node<T> node, Consumer<? super T> consumer) {
                if (node == null) {
                    return;
                }

                traverse(node.left, consumer);
                consumer.accept(node.value);
                traverse(node.right, consumer);
            }
        },
        POST_ORDER {
            @Override
            <T> void traverse(Node<T> node, Consumer<? super T> consumer) {
                if (node == null) {
                    return;
                }

                traverse(node.left, consumer);
                traverse(node.right, consumer);
                consumer.accept(node.value);
            }
        };

        abstract <T> void traverse(Node<T> node, Consumer<? super T> consumer);
    }

    private final Comparator<? super T> comparator;
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

        private int leftHeight() {
            return height(left);
        }

        private int rightHeight() {
            return height(right);
        }

        private int balanceFactor() {
            return leftHeight() - rightHeight();
        }

        private void refreshHeight() {
            height = 1 + Math.max(leftHeight(), rightHeight());
        }
    }

    private static final class InsertionState {
        private boolean inserted;
    }

    public AVLTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public static <T extends Comparable<? super T>> AVLTree<T> createWithNaturalOrder() {
        return new AVLTree<>(Comparator.naturalOrder());
    }

    public boolean insert(T value) {
        Objects.requireNonNull(value, "value");

        InsertionState insertionState = new InsertionState();
        root = insert(root, value, insertionState);

        if (insertionState.inserted) {
            size++;
        }

        return insertionState.inserted;
    }

    public boolean contains(T value) {
        Objects.requireNonNull(value, "value");

        Node<T> current = root;
        while (current != null) {
            int comparison = compare(value, current.value);
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

    public void forEach(TraversalOrder order, Consumer<? super T> consumer) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(consumer, "consumer");

        order.traverse(root, consumer);
    }

    public List<T> traverse(TraversalOrder order) {
        List<T> values = new ArrayList<>();
        forEach(order, values::add);
        return values;
    }

    public List<T> preOrderTraversal() {
        return traverse(TraversalOrder.PRE_ORDER);
    }

    public String joinTraversal(TraversalOrder order, String delimiter) {
        Objects.requireNonNull(delimiter, "delimiter");

        StringBuilder traversal = new StringBuilder();

        forEach(order, value -> {
            if (traversal.length() > 0) {
                traversal.append(delimiter);
            }
            traversal.append(value);
        });

        return traversal.toString();
    }

    public String preOrderString() {
        return joinTraversal(TraversalOrder.PRE_ORDER, " ");
    }

    private Node<T> insert(Node<T> node, T value, InsertionState insertionState) {
        if (node == null) {
            insertionState.inserted = true;
            return new Node<>(value);
        }

        int comparison = compare(value, node.value);
        if (comparison < 0) {
            node.left = insert(node.left, value, insertionState);
        } else if (comparison > 0) {
            node.right = insert(node.right, value, insertionState);
        } else {
            return node;
        }

        return rebalance(node);
    }

    private Node<T> rebalance(Node<T> node) {
        node.refreshHeight();
        int balanceFactor = node.balanceFactor();

        if (balanceFactor > 1) {
            if (node.left.balanceFactor() < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (balanceFactor < -1) {
            if (node.right.balanceFactor() > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> newRoot = node.left;
        Node<T> transferredSubtree = newRoot.right;

        newRoot.right = node;
        node.left = transferredSubtree;

        node.refreshHeight();
        newRoot.refreshHeight();

        return newRoot;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> newRoot = node.right;
        Node<T> transferredSubtree = newRoot.left;

        newRoot.left = node;
        node.right = transferredSubtree;

        node.refreshHeight();
        newRoot.refreshHeight();

        return newRoot;
    }

    private static int height(Node<?> node) {
        return node == null ? 0 : node.height;
    }

    private int compare(T left, T right) {
        return comparator.compare(left, right);
    }
}
