import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class AVLTree<T extends Comparable<? super T>> {
    private static final int MAX_ALLOWED_BALANCE = 1;

    private Node<T> root;

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

    public void insert(T key) {
        Objects.requireNonNull(key, "key");
        root = insert(root, key);
    }

    @SafeVarargs
    public final void insertAll(T... keys) {
        Objects.requireNonNull(keys, "keys");
        for (T key : keys) {
            insert(key);
        }
    }

    @SafeVarargs
    public static <T extends Comparable<? super T>> AVLTree<T> fromKeys(T... keys) {
        Objects.requireNonNull(keys, "keys");
        AVLTree<T> tree = new AVLTree<>();
        for (T key : keys) {
            tree.insert(key);
        }
        return tree;
    }

    public String preOrder() {
        return formatKeys(preOrderKeys());
    }

    public List<T> preOrderKeys() {
        List<T> keys = new ArrayList<>();
        collectPreOrder(root, keys);
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

    private Node<T> insert(Node<T> node, T key) {
        if (node == null) {
            return new Node<>(key);
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = insert(node.left, key);
        } else if (comparison > 0) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }

        updateHeight(node);
        return rebalance(node, key);
    }

    private Node<T> rebalance(Node<T> node, T insertedKey) {
        int nodeBalance = balance(node);

        if (isLeftHeavy(nodeBalance)) {
            if (insertedKey.compareTo(node.left.key) > 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (isRightHeavy(nodeBalance)) {
            if (insertedKey.compareTo(node.right.key) < 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private static boolean isLeftHeavy(int balance) {
        return balance > MAX_ALLOWED_BALANCE;
    }

    private static boolean isRightHeavy(int balance) {
        return balance < -MAX_ALLOWED_BALANCE;
    }

    private static <T> void collectPreOrder(Node<T> node, List<T> keys) {
        if (node == null) {
            return;
        }

        keys.add(node.key);
        collectPreOrder(node.left, keys);
        collectPreOrder(node.right, keys);
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
