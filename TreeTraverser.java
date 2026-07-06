class TreeTraverser<T> {
    private final Node<T> root;

    TreeTraverser(Node<T> root) {
        this.root = root;
    }

    void traverseInOrder(NodeVisitor<T> visitor) {
        inOrder(root, visitor, 0);
    }

    void traversePreOrder(NodeVisitor<T> visitor) {
        preOrder(root, visitor, 0);
    }

    void traversePostOrder(NodeVisitor<T> visitor) {
        postOrder(root, visitor, 0);
    }

    void traverseDescending(NodeVisitor<T> visitor) {
        descending(root, visitor, 0);
    }

    String toVisualString() {
        StringBuilder sb = new StringBuilder();
        traverseDescending((data, depth) ->
            sb.append("    ".repeat(depth)).append(data).append("\n"));
        return sb.toString().stripTrailing();
    }

    private void inOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        inOrder(node.left, visitor, depth + 1);
        visitor.visit(node.data, depth);
        inOrder(node.right, visitor, depth + 1);
    }

    private void preOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        visitor.visit(node.data, depth);
        preOrder(node.left, visitor, depth + 1);
        preOrder(node.right, visitor, depth + 1);
    }

    private void postOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        postOrder(node.left, visitor, depth + 1);
        postOrder(node.right, visitor, depth + 1);
        visitor.visit(node.data, depth);
    }

    private void descending(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        descending(node.right, visitor, depth + 1);
        visitor.visit(node.data, depth);
        descending(node.left, visitor, depth + 1);
    }
}
