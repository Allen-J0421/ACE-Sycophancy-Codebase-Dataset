final class AVLTree {
    private static final class Node {
        final int key;
        Node left;
        Node right;
        int height;
        int size;

        Node(int key) {
            this.key = key;
            this.height = 1;
            this.size = 1;
        }
    }

    private Node root;

    void insert(int key) {
        root = insert(root, key);
    }

    void insertAll(int... keys) {
        for (int key : keys) {
            insert(key);
        }
    }

    static AVLTree of(int... keys) {
        AVLTree tree = new AVLTree();
        tree.insertAll(keys);
        return tree;
    }

    boolean isEmpty() {
        return root == null;
    }

    int size() {
        return size(root);
    }

    int height() {
        return height(root);
    }

    boolean contains(int key) {
        return contains(root, key);
    }

    boolean isValid() {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE).valid();
    }

    void ensureValid() {
        if (!isValid()) {
            throw new IllegalStateException("Tree is not a valid AVL tree");
        }
    }

    String preOrderString() {
        StringBuilder builder = new StringBuilder();
        appendPreOrder(root, builder);
        return builder.toString();
    }

    @Override
    public String toString() {
        return preOrderString();
    }

    private static int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private static int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private static boolean contains(Node node, int key) {
        if (node == null) {
            return false;
        }

        if (key < node.key) {
            return contains(node.left, key);
        }

        if (key > node.key) {
            return contains(node.right, key);
        }

        return true;
    }

    private static void updateMetadata(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
        node.size = 1 + size(node.left) + size(node.right);
    }

    private static int balanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static Node rotateRight(Node pivot) {
        Node newRoot = pivot.left;
        Node transferredSubtree = newRoot.right;

        newRoot.right = pivot;
        pivot.left = transferredSubtree;

        updateMetadata(pivot);
        updateMetadata(newRoot);

        return newRoot;
    }

    private static Node rotateLeft(Node pivot) {
        Node newRoot = pivot.right;
        Node transferredSubtree = newRoot.left;

        newRoot.left = pivot;
        pivot.right = transferredSubtree;

        updateMetadata(pivot);
        updateMetadata(newRoot);

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

        updateMetadata(node);
        return rebalance(node);
    }

    private static void appendKey(StringBuilder builder, int key) {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(key);
    }

    private static void appendPreOrder(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        appendKey(builder, node.key);
        appendPreOrder(node.left, builder);
        appendPreOrder(node.right, builder);
    }

    private static ValidationResult validate(Node node, long min, long max) {
        if (node == null) {
            return ValidationResult.empty();
        }

        if (node.key <= min || node.key >= max) {
            return ValidationResult.invalid();
        }

        ValidationResult left = validate(node.left, min, node.key);
        if (!left.valid()) {
            return left;
        }

        ValidationResult right = validate(node.right, node.key, max);
        if (!right.valid()) {
            return right;
        }

        int expectedHeight = 1 + Math.max(left.height(), right.height());
        int expectedSize = 1 + left.size() + right.size();
        boolean heightMatches = node.height == expectedHeight;
        boolean sizeMatches = node.size == expectedSize;
        boolean balanced = Math.abs(left.height() - right.height()) <= 1;
        return new ValidationResult(
                heightMatches && sizeMatches && balanced,
                expectedHeight,
                expectedSize
        );
    }

    private static record ValidationResult(boolean valid, int height, int size) {

        static ValidationResult empty() {
            return new ValidationResult(true, 0, 0);
        }

        static ValidationResult invalid() {
            return new ValidationResult(false, 0, 0);
        }
    }

    public static void main(String[] args) {
        AVLTree tree = AVLTree.of(10, 20, 30, 40, 50, 25);
        tree.ensureValid();
        System.out.print(tree);
    }
}
