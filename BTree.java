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
            root.keys[0] = key;
            root.keyCount = 1;
        } else if (root.keyCount == 2 * minDegree - 1) {
            BTreeNode newRoot = new BTreeNode(minDegree, false);
            newRoot.children[0] = root;
            newRoot.splitChild(0, root);
            int childIndex = newRoot.keys[0] < key ? 1 : 0;
            newRoot.children[childIndex].insertNonFull(key);
            root = newRoot;
        } else {
            root.insertNonFull(key);
        }
    }
}
