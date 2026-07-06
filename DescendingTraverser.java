class DescendingTraverser<T> implements TreeTraverser<T> {
    @Override
    public void traverse(Node<T> root, NodeVisitor<T> visitor) {
        descending(root, visitor, 0);
    }

    private void descending(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        descending(node.right, visitor, depth + 1);
        visitor.visit(node.data, depth);
        descending(node.left, visitor, depth + 1);
    }
}
