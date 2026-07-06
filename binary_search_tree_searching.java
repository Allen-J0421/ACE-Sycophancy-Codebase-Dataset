class BinarySearchTree<T extends Comparable<T>> implements SearchTree<T> {
    private Node<T> root;

    public void insert(T key) {
        root = insertRec(root, key);
    }

    private Node<T> insertRec(Node<T> node, T key) {
        if (node == null) return new Node<>(key);
        int cmp = key.compareTo(node.data);
        if (cmp < 0)
            node.left = insertRec(node.left, key);
        else if (cmp > 0)
            node.right = insertRec(node.right, key);
        return node;
    }

    public boolean search(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) return true;
            current = cmp > 0 ? current.right : current.left;
        }
        return false;
    }

    @Override
    public String toString() {
        return new TreeTraverser<>(root).toVisualString();
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(6);
        bst.insert(2);
        bst.insert(8);
        bst.insert(7);
        bst.insert(9);

        System.out.println(bst.search(7));
        System.out.println(bst);
    }
}
