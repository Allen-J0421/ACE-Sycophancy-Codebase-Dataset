final class RedBlackTreeFormatter {
    private static final int PRINT_INDENT = 10;

    private RedBlackTreeFormatter() {
    }

    static String formatInorder(RedBlackTreeSnapshot snapshot) {
        return joinValues(snapshot.inorderValues());
    }

    static String formatTree(RedBlackTreeSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();
        appendTree(snapshot.root(), 0, builder);
        return builder.toString();
    }

    private static String joinValues(Iterable<Integer> values) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int value : values) {
            if (!first) {
                builder.append(' ');
            }
            builder.append(value);
            first = false;
        }
        return builder.toString();
    }

    private static void appendTree(RedBlackTreeSnapshot.NodeSnapshot node, int space, StringBuilder builder) {
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
}
