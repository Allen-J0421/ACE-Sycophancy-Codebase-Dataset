class PreOrderTraverser<T> implements TreeTraverser<T> {
    @Override
    public void traverse(Node<T> root, NodeVisitor<T> visitor) {
        preOrder(root, visitor, 0);
    }

    private void preOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        visitor.visit(node.data, depth);
        preOrder(node.left, visitor, depth + 1);
        preOrder(node.right, visitor, depth + 1);
    }
}
