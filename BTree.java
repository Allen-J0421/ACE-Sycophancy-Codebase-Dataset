import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> getKeys() {
        List<Integer> result = new ArrayList<>();
        if (root != null) {
            root.collectKeys(result);
        }
        return result;
    }

    public void traverse() {
        System.out.println(getKeys());
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
