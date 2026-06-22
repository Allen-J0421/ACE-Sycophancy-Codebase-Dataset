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

    public boolean contains(int key) {
        return root != null && root.containsKey(key);
    }

    public void insert(int key) {
        if (root == null) {
            root = new BTreeNode(minDegree, true);
        }
        if (root.isFull()) {
            root = growRoot(key);
        } else {
            root.insert(key);
        }
    }

    private BTreeNode growRoot(int key) {
        BTreeNode newRoot = new BTreeNode(minDegree, false);
        newRoot.children[0] = root;
        newRoot.splitChild(0);
        int childIndex = newRoot.keys[0] < key ? 1 : 0;
        newRoot.children[childIndex].insert(key);
        return newRoot;
    }

    private static class BTreeNode {
        private final int[] keys;
        private final int minDegree;
        private final BTreeNode[] children;
        private int keyCount;
        private final boolean leaf;

        BTreeNode(int minDegree, boolean leaf) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new int[2 * minDegree - 1];
            this.children = new BTreeNode[2 * minDegree];
            this.keyCount = 0;
        }

        void insert(int key) {
            if (leaf) {
                insertIntoLeaf(key);
            } else {
                insertIntoInternalNode(key);
            }
        }

        private void insertIntoLeaf(int key) {
            int i = keyCount - 1;
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            keyCount++;
        }

        private void insertIntoInternalNode(int key) {
            int i = keyCount - 1;
            while (i >= 0 && keys[i] > key) {
                i--;
            }
            int childIndex = i + 1;
            if (children[childIndex].isFull()) {
                splitChild(childIndex);
                if (keys[childIndex] < key) {
                    childIndex++;
                }
            }
            children[childIndex].insert(key);
        }

        void splitChild(int childIndex) {
            BTreeNode child = children[childIndex];
            BTreeNode sibling = createSibling(child);
            child.keyCount = minDegree - 1;
            for (int j = keyCount; j > childIndex; j--) {
                children[j + 1] = children[j];
            }
            children[childIndex + 1] = sibling;
            for (int j = keyCount - 1; j >= childIndex; j--) {
                keys[j + 1] = keys[j];
            }
            keys[childIndex] = child.keys[minDegree - 1];
            keyCount++;
        }

        private BTreeNode createSibling(BTreeNode child) {
            BTreeNode sibling = new BTreeNode(minDegree, child.leaf);
            sibling.keyCount = minDegree - 1;
            for (int j = 0; j < minDegree - 1; j++) {
                sibling.keys[j] = child.keys[j + minDegree];
            }
            if (!child.leaf) {
                for (int j = 0; j < minDegree; j++) {
                    sibling.children[j] = child.children[j + minDegree];
                }
            }
            return sibling;
        }

        void collectKeys(List<Integer> result) {
            if (leaf) {
                for (int i = 0; i < keyCount; i++) {
                    result.add(keys[i]);
                }
            } else {
                for (int i = 0; i < keyCount; i++) {
                    children[i].collectKeys(result);
                    result.add(keys[i]);
                }
                children[keyCount].collectKeys(result);
            }
        }

        boolean containsKey(int key) {
            int i = 0;
            while (i < keyCount && key > keys[i]) {
                i++;
            }
            if (i < keyCount && key == keys[i]) {
                return true;
            }
            return !leaf && children[i].containsKey(key);
        }

        boolean isFull() {
            return keyCount == 2 * minDegree - 1;
        }
    }
}
