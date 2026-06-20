import java.util.ArrayList;
import java.util.List;

class AVLTree<T extends Comparable<T>> {

    private static class Node<T> {
        T key;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T k) {
            key = k;
            height = 1;
        }
    }

    private Node<T> root;

    public void insert(T key) {
        root = insert(root, key);
    }

    public boolean contains(T key) {
        return contains(root, key);
    }

    public List<T> preOrder() {
        List<T> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private static <T> int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private static <T> void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private static <T> int getBalance(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static <T> Node<T> rightRotate(Node<T> y) {
        Node<T> x = y.left;
        Node<T> tmp = x.right;

        x.right = y;
        y.left = tmp;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private static <T> Node<T> leftRotate(Node<T> x) {
        Node<T> y = x.right;
        Node<T> tmp = y.left;

        y.left = x;
        x.right = tmp;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private static <T> Node<T> rebalance(Node<T> node) {
        int balance = getBalance(node);

        // LL case
        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);

        // RR case
        if (balance < -1 && getBalance(node.right) <= 0)
            return leftRotate(node);

        // LR case
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // RL case
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private static <T extends Comparable<T>> Node<T> insert(Node<T> node, T key) {
        if (node == null)
            return new Node<>(key);

        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            node.left = insert(node.left, key);
        else if (cmp > 0)
            node.right = insert(node.right, key);
        else
            return node;

        updateHeight(node);
        return rebalance(node);
    }

    private static <T extends Comparable<T>> boolean contains(Node<T> node, T key) {
        if (node == null)
            return false;

        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            return contains(node.left, key);
        else if (cmp > 0)
            return contains(node.right, key);
        else
            return true;
    }

    private static <T> void preOrder(Node<T> node, List<T> result) {
        if (node != null) {
            result.add(node.key);
            preOrder(node.left, result);
            preOrder(node.right, result);
        }
    }

    private static <T> void inOrder(Node<T> node, List<T> result) {
        if (node != null) {
            inOrder(node.left, result);
            result.add(node.key);
            inOrder(node.right, result);
        }
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        System.out.println(tree.preOrder());
        System.out.println(tree.inOrder());
        System.out.println(tree.contains(25));
        System.out.println(tree.contains(99));
    }
}
