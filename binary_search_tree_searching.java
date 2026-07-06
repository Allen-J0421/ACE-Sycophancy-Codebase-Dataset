class BinarySearchTree<T extends Comparable<T>> implements SearchTree<T> {
    private Node<T> root;

    public void insert(T key) {
        root = insertRec(root, key);
    }

    private Node<T> insertRec(Node<T> node, T key) {
        if (node == null) return new Node<>(key);
        int cmp = key.compareTo(node.getData());
        if (cmp < 0)
            node.setLeft(insertRec(node.getLeft(), key));
        else if (cmp > 0)
            node.setRight(insertRec(node.getRight(), key));
        return node;
    }

    public boolean search(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.getData());
            if (cmp == 0) return true;
            current = cmp > 0 ? current.getRight() : current.getLeft();
        }
        return false;
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(6);
        bst.insert(2);
        bst.insert(8);
        bst.insert(7);
        bst.insert(9);

        int key = 7;
        System.out.println(bst.search(key));
    }
}
