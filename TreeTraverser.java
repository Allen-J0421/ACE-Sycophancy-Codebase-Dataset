interface TreeTraverser<T> {
    void traverse(Node<T> root, NodeVisitor<T> visitor);
}
