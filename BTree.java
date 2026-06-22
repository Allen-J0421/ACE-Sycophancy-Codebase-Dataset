final class BTree {
    private final int minDegree;
    private BTreeNode root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
    }

    void traverse() {
        System.out.print(traversalString());
    }

    String traversalString() {
        return root == null ? "" : root.traversalString();
    }

    BTreeNode search(int key) {
        return root == null ? null : root.search(key);
    }

    boolean contains(int key) {
        return search(key) != null;
    }

    void insert(int key) {
        if (root == null) {
            root = new BTreeNode(minDegree, true);
            root.insertNonFull(key);
            return;
        }

        if (root.isFull()) {
            BTreeNode newRoot = new BTreeNode(minDegree, false);
            newRoot.splitChild(0, root);
            newRoot.insertNonFull(key);
            root = newRoot;
            return;
        }

        root.insertNonFull(key);
    }
}
