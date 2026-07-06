class BinarySearchTree<T extends Comparable<T>> implements SearchTree<T> {
    private Node<T> root;
    private TreeTraverser<T> traverser;

    BinarySearchTree() {
        this(new InOrderTraverser<>());
    }

    BinarySearchTree(TreeTraverser<T> traverser) {
        this.traverser = traverser;
    }

    void setTraverser(TreeTraverser<T> traverser) {
        this.traverser = traverser;
    }

    void traverse(NodeVisitor<T> visitor) {
        traverser.traverse(root, visitor);
    }

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
        StringBuilder sb = new StringBuilder();
        new DescendingTraverser<T>().traverse(root, (data, depth) ->
            sb.append("    ".repeat(depth)).append(data).append("\n"));
        return sb.toString().stripTrailing();
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

        System.out.print("In-order:   ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();

        bst.setTraverser(new PreOrderTraverser<>());
        System.out.print("Pre-order:  ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();

        bst.setTraverser(new PostOrderTraverser<>());
        System.out.print("Post-order: ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();
    }
}
