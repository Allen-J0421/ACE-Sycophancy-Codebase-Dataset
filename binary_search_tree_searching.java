class BinarySearchTree<T extends Comparable<T>> implements SearchTree<T> {
    private Node<T> root;
    private TreeTraverser<T> traverser;
    private SearchCallback<T> searchCallback = SearchCallback.noOp();

    BinarySearchTree() {
        this(TreeTraverserFactory.inOrder());
    }

    BinarySearchTree(TreeTraverser<T> traverser) {
        this.traverser = traverser;
    }

    void setTraverser(TreeTraverser<T> traverser) {
        this.traverser = traverser;
    }

    void setSearchCallback(SearchCallback<T> callback) {
        this.searchCallback = callback;
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
        return search(key, searchCallback);
    }

    boolean search(T key, SearchCallback<T> callback) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) {
                callback.onFound(key);
                return true;
            }
            current = cmp > 0 ? current.right : current.left;
        }
        callback.onNotFound(key);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TreeTraverserFactory.<T>descending().traverse(root, (data, depth) ->
            sb.append("    ".repeat(depth)).append(data).append("\n"));
        return sb.toString().stripTrailing();
    }

    public static void main(String[] args) {
        SearchCallback<Integer> logger = new SearchCallback<Integer>() {
            @Override void onFound(Integer key) { System.out.println("[LOG] Found: " + key); }
            @Override void onNotFound(Integer key) { System.out.println("[LOG] Not found: " + key); }
        };

        SearchCallback<Integer> analytics = new SearchCallback<Integer>() {
            private int hits = 0, misses = 0;
            @Override void onFound(Integer key) { System.out.println("[ANALYTICS] hits=" + ++hits); }
            @Override void onNotFound(Integer key) { System.out.println("[ANALYTICS] misses=" + ++misses); }
        };

        BinarySearchTree<Integer> bst = new TreeBuilder<Integer>()
            .insert(6)
            .insert(2)
            .insert(8)
            .insert(7)
            .insert(9)
            .withSearchCallback(logger.andThen(analytics))
            .build();

        System.out.println(bst);

        bst.search(7);
        bst.search(5);
        bst.search(9);

        System.out.print("In-order:   ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();

        bst.setTraverser(TreeTraverserFactory.preOrder());
        System.out.print("Pre-order:  ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();

        bst.setTraverser(TreeTraverserFactory.postOrder());
        System.out.print("Post-order: ");
        bst.traverse((data, depth) -> System.out.print(data + " "));
        System.out.println();
    }
}
