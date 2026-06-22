public class BTree {
    private BTreeNode root;
    private final int minDegree;

    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("Minimum degree must be at least 2");
        }
        this.minDegree = minDegree;
        this.root = null;
    }

    public void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    public boolean contains(int key) {
        return root != null && root.search(key) != null;
    }

    public void insert(int key) {
        if (root == null) {
            root = new BTreeNode(minDegree, true);
            root.insertNonFull(key);
        } else if (root.isFull()) {
            root = BTreeNode.splitRoot(root, key);
        } else {
            root.insertNonFull(key);
        }
    }
}
