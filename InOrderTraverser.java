class InOrderTraverser<T> implements TreeTraverser<T> {
    @Override
    public void traverse(Node<T> root, NodeVisitor<T> visitor) {
        inOrder(root, visitor, 0);
    }

    private void inOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        inOrder(node.left, visitor, depth + 1);
        visitor.visit(node.data, depth);
        inOrder(node.right, visitor, depth + 1);
    }
}
