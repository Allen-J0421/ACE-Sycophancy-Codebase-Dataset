import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

final class RedBlackTreeSnapshot {
    private static final int PRINT_INDENT = 10;

    private record ValidationResult(
        boolean valid,
        int blackHeight,
        long minValue,
        long maxValue,
        boolean empty
    ) {
    }

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
        traverseInorder(root, values::add);
        return values;
    }

    String inorderString() {
        StringBuilder builder = new StringBuilder();
        traverseInorder(root, value -> {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(value);
        });
        return builder.toString();
    }

    String formatTree() {
        StringBuilder builder = new StringBuilder();
        appendTree(root, 0, builder);
        return builder.toString();
    }

    boolean isValid() {
        return validate(root).valid();
    }

    private void traverseInorder(NodeSnapshot node, IntConsumer visitor) {
        if (node == null) {
            return;
        }

        traverseInorder(node.left(), visitor);
        visitor.accept(node.data());
        traverseInorder(node.right(), visitor);
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

    private ValidationResult validate(NodeSnapshot node) {
        if (node == null) {
            return new ValidationResult(true, 1, 0, 0, true);
        }

        ValidationResult left = validate(node.left());
        ValidationResult right = validate(node.right());
        if (!left.valid() || !right.valid()) {
            return invalid();
        }

        if (!left.empty() && left.maxValue() >= node.data()) {
            return invalid();
        }
        if (!right.empty() && right.minValue() < node.data()) {
            return invalid();
        }
        if (isRed(node) && (isRed(node.left()) || isRed(node.right()))) {
            return invalid();
        }
        if (left.blackHeight() != right.blackHeight()) {
            return invalid();
        }

        int blackHeight = left.blackHeight() + (node.color() == RedBlackTreeColor.BLACK ? 1 : 0);
        long minValue = left.empty() ? node.data() : left.minValue();
        long maxValue = right.empty() ? node.data() : right.maxValue();
        boolean rootBlack = node == root ? node.color() == RedBlackTreeColor.BLACK : true;
        return new ValidationResult(rootBlack, blackHeight, minValue, maxValue, false);
    }

    private boolean isRed(NodeSnapshot node) {
        return node != null && node.color() == RedBlackTreeColor.RED;
    }

    private ValidationResult invalid() {
        return new ValidationResult(false, -1, 0, 0, false);
    }
}
