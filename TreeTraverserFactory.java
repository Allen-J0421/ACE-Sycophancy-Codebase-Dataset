class TreeTraverserFactory {
    private TreeTraverserFactory() {}

    static <T> TreeTraverser<T> inOrder() {
        return new InOrderTraverser<>();
    }

    static <T> TreeTraverser<T> preOrder() {
        return new PreOrderTraverser<>();
    }

    static <T> TreeTraverser<T> postOrder() {
        return new PostOrderTraverser<>();
    }

    static <T> TreeTraverser<T> descending() {
        return new DescendingTraverser<>();
    }
}
