final class RedBlackTreeFormatter {
    private static final int PRINT_INDENT = 10;

    private RedBlackTreeFormatter() {
    }

    static String formatInorder(RedBlackTreeSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();
        appendInorder(snapshot.root(), builder);
        return builder.toString();
    }

    static String formatTree(RedBlackTreeSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();
        appendTree(snapshot.root(), 0, builder);
        return builder.toString();
    }

    private static void appendInorder(RedBlackTreeSnapshot.NodeSnapshot node, StringBuilder builder) {
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
