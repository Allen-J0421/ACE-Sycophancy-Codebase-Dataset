import java.util.ArrayList;
import java.util.List;

final class RedBlackTreeSnapshot {
    private static final int PRINT_INDENT = 10;

    record NodeSnapshot(
        int data,
        RedBlackTreeColor color,
        NodeSnapshot left,
        NodeSnapshot right
    ) {
    }

    private final NodeSnapshot root;

    RedBlackTreeSnapshot(NodeSnapshot root) {
        this.root = root;
    }

    NodeSnapshot root() {
        return root;
    }

    List<Integer> inorderValues() {
        List<Integer> values = new ArrayList<>();
        collectInorder(root, values);
        return values;
    }

    String inorderString() {
        StringBuilder builder = new StringBuilder();
        appendInorder(root, builder);
        return builder.toString();
    }

    String formatTree() {
        StringBuilder builder = new StringBuilder();
        appendTree(root, 0, builder);
        return builder.toString();
    }

    boolean isValid() {
        if (root == null) {
            return true;
        }

        if (root.color() != RedBlackTreeColor.BLACK) {
            return false;
        }

        return isBinarySearchTree(root, Long.MIN_VALUE, Long.MAX_VALUE)
            && hasNoRedRedViolation(root)
            && blackHeight(root) != -1;
    }

    private void collectInorder(NodeSnapshot node, List<Integer> values) {
        if (node == null) {
            return;
        }

        collectInorder(node.left(), values);
        values.add(node.data());
        collectInorder(node.right(), values);
    }

    private void appendInorder(NodeSnapshot node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        appendInorder(node.left(), builder);
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(node.data());
        appendInorder(node.right(), builder);
    }

    private void appendTree(NodeSnapshot node, int space, StringBuilder builder) {
        if (node == null) {
            return;
        }

        int nextSpace = space + PRINT_INDENT;
        appendTree(node.right(), nextSpace, builder);
        builder.append('\n');
        for (int i = PRINT_INDENT; i < nextSpace; i++) {
            builder.append(' ');
        }
        builder.append(node.data()).append('\n');
        appendTree(node.left(), nextSpace, builder);
    }

    private boolean isBinarySearchTree(NodeSnapshot node, long minInclusive, long maxExclusive) {
        if (node == null) {
            return true;
        }

        if (node.data() < minInclusive || node.data() >= maxExclusive) {
            return false;
        }

        return isBinarySearchTree(node.left(), minInclusive, node.data())
            && isBinarySearchTree(node.right(), node.data(), maxExclusive);
    }

    private boolean hasNoRedRedViolation(NodeSnapshot node) {
        if (node == null) {
            return true;
        }

        if (isRed(node) && (isRed(node.left()) || isRed(node.right()))) {
            return false;
        }

        return hasNoRedRedViolation(node.left()) && hasNoRedRedViolation(node.right());
    }

    private int blackHeight(NodeSnapshot node) {
        if (node == null) {
            return 1;
        }

        int leftHeight = blackHeight(node.left());
        int rightHeight = blackHeight(node.right());
        if (leftHeight == -1 || rightHeight == -1 || leftHeight != rightHeight) {
            return -1;
        }

        return leftHeight + (node.color() == RedBlackTreeColor.BLACK ? 1 : 0);
    }

    private boolean isRed(NodeSnapshot node) {
        return node != null && node.color() == RedBlackTreeColor.RED;
    }
}
