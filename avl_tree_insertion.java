class AVLTree {
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

    public String preOrder() {
        StringBuilder result = new StringBuilder();
        appendPreOrder(root, result);
        return result.toString();
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

        int nodeBalance = balance(node);

        if (nodeBalance > 1 && key < node.left.key) {
            return rotateRight(node);
        }

        if (nodeBalance < -1 && key > node.right.key) {
            return rotateLeft(node);
        }

        if (nodeBalance > 1 && key > node.left.key) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (nodeBalance < -1 && key < node.right.key) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private static void appendPreOrder(Node node, StringBuilder result) {
        if (node == null) {
            return;
        }

        if (result.length() > 0) {
            result.append(' ');
        }

        result.append(node.key);
        appendPreOrder(node.left, result);
        appendPreOrder(node.right, result);
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        int[] values = {10, 20, 30, 40, 50, 25};

        for (int value : values) {
            tree.insert(value);
        }

        System.out.print(tree.preOrder());
    }
}
