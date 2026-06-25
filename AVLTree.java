import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

public final class AVLTree<T> {
    private static final int MAX_ALLOWED_BALANCE = 1;

    private final Comparator<? super T> comparator;
    private Node<T> root;
    private int size;

    private static final class Node<T> {
        private final T key;
        private Node<T> left;
        private Node<T> right;
        private int height;

        private Node(T key) {
            this.key = key;
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

    public AVLTree(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public boolean insert(T key) {
        Objects.requireNonNull(key, "key");
        InsertResult<T> result = insert(root, key);
        root = result.node;
        if (result.inserted) {
            size++;
        }
        return result.inserted;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final void insertAll(T... keys) {
        Objects.requireNonNull(keys, "keys");
        insertKeys(keys);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T extends Comparable<? super T>> AVLTree<T> fromKeys(T... keys) {
        Objects.requireNonNull(keys, "keys");
        AVLTree<T> tree = new AVLTree<>(Comparator.naturalOrder());
        tree.insertKeys(keys);
        return tree;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> AVLTree<T> fromKeys(Comparator<? super T> comparator, T... keys) {
        Objects.requireNonNull(keys, "keys");
        AVLTree<T> tree = new AVLTree<>(comparator);
        tree.insertKeys(keys);
        return tree;
    }

    public boolean contains(T key) {
        Objects.requireNonNull(key, "key");
        return findNode(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String preOrder() {
        return formatKeys(preOrderKeys());
    }

    public void forEachPreOrder(Consumer<? super T> visitor) {
        Objects.requireNonNull(visitor, "visitor");
        traversePreOrder(root, visitor);
    }

    public List<T> preOrderKeys() {
        List<T> keys = new ArrayList<>(size);
        forEachPreOrder(keys::add);
        return Collections.unmodifiableList(keys);
    }

    private static int height(Node<?> node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private static void updateHeight(Node<?> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> newRoot = node.left;
        Node<T> subtree = newRoot.right;

        newRoot.right = node;
        node.left = subtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> newRoot = node.right;
        Node<T> subtree = newRoot.left;

        newRoot.left = node;
        node.right = subtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private static int balance(Node<?> node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private void insertKeys(T[] keys) {
        for (T key : keys) {
            insert(key);
        }
    }

    private Node<T> findNode(T key) {
        Node<T> current = root;

        while (current != null) {
            int comparison = compare(key, current.key);

            if (comparison < 0) {
                current = current.left;
            } else if (comparison > 0) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    private InsertResult<T> insert(Node<T> node, T key) {
        if (node == null) {
            return new InsertResult<>(new Node<>(key), true);
        }

        int comparison = compare(key, node.key);

        if (comparison < 0) {
            InsertResult<T> result = insert(node.left, key);
            if (!result.inserted) {
                return new InsertResult<>(node, false);
            }
            node.left = result.node;
        } else if (comparison > 0) {
            InsertResult<T> result = insert(node.right, key);
            if (!result.inserted) {
                return new InsertResult<>(node, false);
            }
            node.right = result.node;
        } else {
            return new InsertResult<>(node, false);
        }

        updateHeight(node);
        return new InsertResult<>(rebalance(node, key), true);
    }

    private Node<T> rebalance(Node<T> node, T insertedKey) {
        int nodeBalance = balance(node);

        if (isLeftHeavy(nodeBalance)) {
            if (compare(insertedKey, node.left.key) > 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (isRightHeavy(nodeBalance)) {
            if (compare(insertedKey, node.right.key) < 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private int compare(T left, T right) {
        return comparator.compare(left, right);
    }

    private static boolean isLeftHeavy(int balance) {
        return balance > MAX_ALLOWED_BALANCE;
    }

    private static boolean isRightHeavy(int balance) {
        return balance < -MAX_ALLOWED_BALANCE;
    }

    private static <T> void traversePreOrder(Node<T> node, Consumer<? super T> visitor) {
        if (node == null) {
            return;
        }

        visitor.accept(node.key);
        traversePreOrder(node.left, visitor);
        traversePreOrder(node.right, visitor);
    }

    private static String formatKeys(List<?> keys) {
        StringJoiner result = new StringJoiner(" ");
        for (Object key : keys) {
            result.add(String.valueOf(key));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        AvlTreeInsertionDemo.main(args);
    }
}
