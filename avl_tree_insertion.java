import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

class AVLTree {
    private static final int MAX_ALLOWED_BALANCE = 1;

    private Node root;

    private static final class Node {
        private final int key;
        private Node left;
        private Node right;
        private int height;

        private Node(int key) {
            this.key = key;
            this.height = 1;
        }
    }

    public void insert(int key) {
        root = insert(root, key);
    }

    public void insertAll(int... keys) {
        for (int key : keys) {
            insert(key);
        }
    }

    public String preOrder() {
        return formatKeys(preOrderKeys());
    }

    public List<Integer> preOrderKeys() {
        List<Integer> keys = new ArrayList<>();
        collectPreOrder(root, keys);
        return Collections.unmodifiableList(keys);
    }

    private static int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private static void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private static Node rotateRight(Node node) {
        Node newRoot = node.left;
        Node subtree = newRoot.right;

        newRoot.right = node;
        node.left = subtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private static Node rotateLeft(Node node) {
        Node newRoot = node.right;
        Node subtree = newRoot.left;

        newRoot.left = node;
        node.right = subtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private static int balance(Node node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    private static Node insert(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }

        updateHeight(node);
        return rebalance(node, key);
    }

    private static Node rebalance(Node node, int insertedKey) {
        int nodeBalance = balance(node);

        if (isLeftHeavy(nodeBalance)) {
            if (insertedKey > node.left.key) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (isRightHeavy(nodeBalance)) {
            if (insertedKey < node.right.key) {
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

    private static void collectPreOrder(Node node, List<Integer> keys) {
        if (node == null) {
            return;
        }

        keys.add(node.key);
        collectPreOrder(node.left, keys);
        collectPreOrder(node.right, keys);
    }

    private static String formatKeys(List<Integer> keys) {
        StringJoiner result = new StringJoiner(" ");
        for (int key : keys) {
            result.add(String.valueOf(key));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        tree.insertAll(10, 20, 30, 40, 50, 25);

        System.out.print(tree.preOrder());
    }
}
