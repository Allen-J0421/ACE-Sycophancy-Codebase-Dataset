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

    void insertAll(int... keys) {
        for (int key : keys) {
            insert(key);
        }
    }

    boolean isEmpty() {
        return root == null;
    }

    boolean isValid() {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE).valid;
    }

    void preOrder() {
        System.out.print(this);
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
        if (!left.valid) {
            return left;
        }

        ValidationResult right = validate(node.right, node.key, max);
        if (!right.valid) {
            return right;
        }

        int expectedHeight = 1 + Math.max(left.height, right.height);
        boolean heightMatches = node.height == expectedHeight;
        boolean balanced = Math.abs(left.height - right.height) <= 1;
        return new ValidationResult(
                heightMatches && balanced,
                expectedHeight
        );
    }

    private static final class ValidationResult {
        final boolean valid;
        final int height;

        ValidationResult(boolean valid, int height) {
            this.valid = valid;
            this.height = height;
        }

        static ValidationResult empty() {
            return new ValidationResult(true, 0);
        }

        static ValidationResult invalid() {
            return new ValidationResult(false, 0);
        }
    }

    private static AVLTree buildDemoTree() {
        AVLTree tree = new AVLTree();
        tree.insertAll(10, 20, 30, 40, 50, 25);
        if (!tree.isValid()) {
            throw new IllegalStateException("Demo tree is not a valid AVL tree");
        }
        return tree;
    }

    public static void main(String[] args) {
        AVLTree tree = buildDemoTree();
        System.out.print(tree);
    }
}
