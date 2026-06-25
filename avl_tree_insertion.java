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

    public String preOrderTraversal() {
        StringBuilder traversal = new StringBuilder();
        buildPreOrderTraversal(root, traversal);
        return traversal.toString().trim();
    }

    private Node insert(Node node, int key) {
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

    private Node rebalance(Node node, int insertedKey) {
        int balanceFactor = getBalanceFactor(node);

        if (balanceFactor > 1 && insertedKey < node.left.key) {
            return rotateRight(node);
        }

        if (balanceFactor < -1 && insertedKey > node.right.key) {
            return rotateLeft(node);
        }

        if (balanceFactor > 1 && insertedKey > node.left.key) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balanceFactor < -1 && insertedKey < node.right.key) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private void buildPreOrderTraversal(Node node, StringBuilder traversal) {
        if (node == null) {
            return;
        }

        if (traversal.length() > 0) {
            traversal.append(' ');
        }

        traversal.append(node.key);
        buildPreOrderTraversal(node.left, traversal);
        buildPreOrderTraversal(node.right, traversal);
    }

    private Node rotateRight(Node node) {
        Node newRoot = node.left;
        Node transferredSubtree = newRoot.right;

        newRoot.right = node;
        node.left = transferredSubtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private Node rotateLeft(Node node) {
        Node newRoot = node.right;
        Node transferredSubtree = newRoot.left;

        newRoot.left = node;
        node.right = transferredSubtree;

        updateHeight(node);
        updateHeight(newRoot);

        return newRoot;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int getBalanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        int[] keys = {10, 20, 30, 40, 50, 25};

        for (int key : keys) {
            tree.insert(key);
        }

        System.out.println(tree.preOrderTraversal());
    }
}
