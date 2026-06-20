import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

class AVLTree<T extends Comparable<T>> implements Iterable<T> {

    private static class Node<T> {
        final T key;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T key) {
            this.key = key;
            this.height = 1;
        }
    }

    private Node<T> root;
    private int size;

    public boolean insert(T key) {
        Objects.requireNonNull(key, "key must not be null");
        if (contains(key)) return false;
        root = insert(root, key);
        size++;
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Deque<Node<T>> stack = new ArrayDeque<>();

            { pushLeft(root); }

            private void pushLeft(Node<T> node) {
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                Node<T> node = stack.pop();
                pushLeft(node.right);
                return node.key;
            }
        };
    }

    public boolean contains(T key) {
        Objects.requireNonNull(key, "key must not be null");
        Node<T> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0)
                node = node.left;
            else if (cmp > 0)
                node = node.right;
            else
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return inOrder().toString();
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

        if (balance > 1) {
            if (getBalance(node.left) < 0)      // LR: fix left child first
                node.left = leftRotate(node.left);
            return rightRotate(node);            // LL (or LR after fix)
        }

        if (balance < -1) {
            if (getBalance(node.right) > 0)     // RL: fix right child first
                node.right = rightRotate(node.right);
            return leftRotate(node);             // RR (or RL after fix)
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

        System.out.println("preOrder:    " + tree.preOrder());
        System.out.println("inOrder:     " + tree);
        System.out.println("size:        " + tree.size());
        System.out.println("isEmpty:     " + tree.isEmpty());
        System.out.println("contains 25: " + tree.contains(25));
        System.out.println("contains 99: " + tree.contains(99));
        System.out.println("insert 25:   " + tree.insert(25));
        System.out.println("insert 35:   " + tree.insert(35));
        System.out.println("size:        " + tree.size());

        System.out.print("for-each:    ");
        for (int key : tree)
            System.out.print(key + " ");
        System.out.println();
    }
}
