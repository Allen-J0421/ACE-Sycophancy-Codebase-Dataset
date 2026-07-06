class TreeBuilder<T extends Comparable<T>> {
    private final BinarySearchTree<T> tree = new BinarySearchTree<>();

    TreeBuilder<T> insert(T key) {
        tree.insert(key);
        return this;
    }

    TreeBuilder<T> withTraverser(TreeTraverser<T> traverser) {
        tree.setTraverser(traverser);
        return this;
    }

    BinarySearchTree<T> build() {
        return tree;
    }
}
