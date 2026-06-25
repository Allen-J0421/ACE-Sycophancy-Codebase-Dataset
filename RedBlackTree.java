import java.util.ArrayList;
import java.util.List;

public final class RedBlackTree {
    private static final int PRINT_INDENT = 10;

    private enum Color {
        RED,
        BLACK
    }

    private static final class Node {
        private final int data;
        private Node left;
        private Node right;
        private Node parent;
        private Color color;

        private Node(int data, Color color) {
            this.data = data;
            this.color = color;
        }
    }

    private static final class InsertionState {
        private boolean rotateLeftLeft;
        private boolean rotateRightRight;
        private boolean rotateLeftRight;
        private boolean rotateRightLeft;
    }

    private Node root;

    public void insert(int data) {
        if (root == null) {
            root = new Node(data, Color.BLACK);
            return;
        }

        InsertionState state = new InsertionState();
        root = insert(root, data, state);
        root.color = Color.BLACK;
        root.parent = null;
    }

    public List<Integer> inorderValues() {
        List<Integer> values = new ArrayList<>();
        collectInorder(root, values);
        return values;
    }

    public String inorderString() {
        List<Integer> values = inorderValues();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }

    public void inorderTraversal() {
        System.out.print(inorderString());
    }

    public String formatTree() {
        StringBuilder builder = new StringBuilder();
        appendTree(root, 0, builder);
        return builder.toString();
    }

    public void printTree() {
        System.out.print(formatTree());
    }

    public boolean isValidRedBlackTree() {
        if (root == null) {
            return true;
        }

        if (root.color != Color.BLACK) {
            return false;
        }

        return isBinarySearchTree(root, Long.MIN_VALUE, Long.MAX_VALUE)
            && hasNoRedRedViolation(root)
            && blackHeight(root) != -1;
    }

    private Node insert(Node current, int data, InsertionState state) {
        boolean redRedConflict = false;

        if (current == null) {
            return new Node(data, Color.RED);
        }

        if (data < current.data) {
            current.left = insert(current.left, data, state);
            current.left.parent = current;
            redRedConflict = current != root && isRed(current) && isRed(current.left);
        } else {
            current.right = insert(current.right, data, state);
            current.right.parent = current;
            redRedConflict = current != root && isRed(current) && isRed(current.right);
        }

        current = applyPendingRotation(current, state);

        if (redRedConflict) {
            resolveRedRedConflict(current, state);
        }

        return current;
    }

    private Node applyPendingRotation(Node current, InsertionState state) {
        if (state.rotateLeftLeft) {
            current = rotateLeft(current);
            recolorAfterSingleRotation(current, current.left);
            state.rotateLeftLeft = false;
        } else if (state.rotateRightRight) {
            current = rotateRight(current);
            recolorAfterSingleRotation(current, current.right);
            state.rotateRightRight = false;
        } else if (state.rotateRightLeft) {
            current.right = rotateRight(current.right);
            current.right.parent = current;
            current = rotateLeft(current);
            recolorAfterSingleRotation(current, current.left);
            state.rotateRightLeft = false;
        } else if (state.rotateLeftRight) {
            current.left = rotateLeft(current.left);
            current.left.parent = current;
            current = rotateRight(current);
            recolorAfterSingleRotation(current, current.right);
            state.rotateLeftRight = false;
        }

        return current;
    }

    private void resolveRedRedConflict(Node current, InsertionState state) {
        Node parent = current.parent;
        if (parent.right == current) {
            if (isBlack(parent.left)) {
                if (isRed(current.left)) {
                    state.rotateRightLeft = true;
                } else if (isRed(current.right)) {
                    state.rotateLeftLeft = true;
                }
            } else {
                recolorForRedUncle(current, parent.left);
            }
        } else {
            if (isBlack(parent.right)) {
                if (isRed(current.left)) {
                    state.rotateRightRight = true;
                } else if (isRed(current.right)) {
                    state.rotateLeftRight = true;
                }
            } else {
                recolorForRedUncle(current, parent.right);
            }
        }
    }

    private void recolorAfterSingleRotation(Node newRoot, Node childToRed) {
        newRoot.color = Color.BLACK;
        childToRed.color = Color.RED;
    }

    private void recolorForRedUncle(Node current, Node uncle) {
        uncle.color = Color.BLACK;
        current.color = Color.BLACK;
        if (current.parent != root) {
            current.parent.color = Color.RED;
        }
    }

    private Node rotateLeft(Node node) {
        Node pivot = node.right;
        Node transferredSubtree = pivot.left;

        pivot.left = node;
        pivot.parent = node.parent;
        node.parent = pivot;
        node.right = transferredSubtree;

        if (transferredSubtree != null) {
            transferredSubtree.parent = node;
        }

        return pivot;
    }

    private Node rotateRight(Node node) {
        Node pivot = node.left;
        Node transferredSubtree = pivot.right;

        pivot.right = node;
        pivot.parent = node.parent;
        node.parent = pivot;
        node.left = transferredSubtree;

        if (transferredSubtree != null) {
            transferredSubtree.parent = node;
        }

        return pivot;
    }

    private void collectInorder(Node node, List<Integer> values) {
        if (node == null) {
            return;
        }

        collectInorder(node.left, values);
        values.add(node.data);
        collectInorder(node.right, values);
    }

    private void appendTree(Node node, int space, StringBuilder builder) {
        if (node == null) {
            return;
        }

        int nextSpace = space + PRINT_INDENT;
        appendTree(node.right, nextSpace, builder);
        builder.append('\n');
        for (int i = PRINT_INDENT; i < nextSpace; i++) {
            builder.append(' ');
        }
        builder.append(node.data).append('\n');
        appendTree(node.left, nextSpace, builder);
    }

    private boolean isBinarySearchTree(Node node, long minInclusive, long maxExclusive) {
        if (node == null) {
            return true;
        }

        if (node.data < minInclusive || node.data >= maxExclusive) {
            return false;
        }

        return isBinarySearchTree(node.left, minInclusive, node.data)
            && isBinarySearchTree(node.right, node.data, maxExclusive);
    }

    private boolean hasNoRedRedViolation(Node node) {
        if (node == null) {
            return true;
        }

        if (isRed(node) && (isRed(node.left) || isRed(node.right))) {
            return false;
        }

        return hasNoRedRedViolation(node.left) && hasNoRedRedViolation(node.right);
    }

    private int blackHeight(Node node) {
        if (node == null) {
            return 1;
        }

        int leftHeight = blackHeight(node.left);
        int rightHeight = blackHeight(node.right);
        if (leftHeight == -1 || rightHeight == -1 || leftHeight != rightHeight) {
            return -1;
        }

        return leftHeight + (node.color == Color.BLACK ? 1 : 0);
    }

    private boolean isRed(Node node) {
        return node != null && node.color == Color.RED;
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == Color.BLACK;
    }
}
