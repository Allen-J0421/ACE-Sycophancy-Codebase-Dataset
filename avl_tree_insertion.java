final class AVLTree {
    private static final class Node {
        final int key;
        Node left;
        Node right;
        int height;

        Node(int key) {
            this.key = key;
            this.height = 1;
        }
    }

    private Node root;

    void insert(int key) {
        root = insert(root, key);
    }

    void preOrder() {
        preOrder(root);
    }

    private static int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private static void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private static int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static Node rotateRight(Node pivot) {
        Node newRoot = pivot.left;
        Node transferredSubtree = newRoot.right;

        newRoot.right = pivot;
        pivot.left = transferredSubtree;

        updateHeight(pivot);
        updateHeight(newRoot);

        return newRoot;
    }

    private static Node rotateLeft(Node pivot) {
        Node newRoot = pivot.right;
        Node transferredSubtree = newRoot.left;

        newRoot.left = pivot;
        pivot.right = transferredSubtree;

        updateHeight(pivot);
        updateHeight(newRoot);

        return newRoot;
    }

    private static Node rebalance(Node node) {
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
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
        return rebalance(node);
    }

    private static void preOrder(Node node) {
        if (node == null) {
            return;
        }

        System.out.print(node.key + " ");
        preOrder(node.left);
        preOrder(node.right);
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        tree.preOrder();
    }
}
