class PostOrderTraverser<T> implements TreeTraverser<T> {
    @Override
    public void traverse(Node<T> root, NodeVisitor<T> visitor) {
        postOrder(root, visitor, 0);
    }

    private void postOrder(Node<T> node, NodeVisitor<T> visitor, int depth) {
        if (node == null) return;
        postOrder(node.left, visitor, depth + 1);
        postOrder(node.right, visitor, depth + 1);
        visitor.visit(node.data, depth);
    }
}
