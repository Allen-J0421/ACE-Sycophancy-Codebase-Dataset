class TreeTraverser<T> {
    private final Node<T> root;

    TreeTraverser(Node<T> root) {
        this.root = root;
    }

    String toVisualString() {
        StringBuilder sb = new StringBuilder();
        buildString(root, sb, 0);
        return sb.toString().stripTrailing();
    }

    private void buildString(Node<T> node, StringBuilder sb, int depth) {
        if (node == null) return;
        buildString(node.right, sb, depth + 1);
        sb.append("    ".repeat(depth)).append(node.data).append("\n");
        buildString(node.left, sb, depth + 1);
    }
}
