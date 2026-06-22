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
        return root != null && root.containsKey(key);
    }

    public void insert(int key) {
        if (root == null) {
            root = new BTreeNode(minDegree, true);
            root.insert(key);
        } else if (root.isFull()) {
            root = growRoot(key);
        } else {
            root.insert(key);
        }
    }

    private BTreeNode growRoot(int key) {
        BTreeNode newRoot = new BTreeNode(minDegree, false);
        newRoot.children[0] = root;
        newRoot.splitChild(0, root);
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
            int i = keyCount - 1;
            if (leaf) {
                while (i >= 0 && keys[i] > key) {
                    keys[i + 1] = keys[i];
                    i--;
                }
                keys[i + 1] = key;
                keyCount++;
            } else {
                while (i >= 0 && keys[i] > key) {
                    i--;
                }
                int childIndex = i + 1;
                if (children[childIndex].isFull()) {
                    splitChild(childIndex, children[childIndex]);
                    if (keys[childIndex] < key) {
                        childIndex++;
                    }
                }
                children[childIndex].insert(key);
            }
        }

        void splitChild(int childIndex, BTreeNode child) {
            BTreeNode newChild = new BTreeNode(minDegree, child.leaf);
            newChild.keyCount = minDegree - 1;
            for (int j = 0; j < minDegree - 1; j++) {
                newChild.keys[j] = child.keys[j + minDegree];
            }
            if (!child.leaf) {
                for (int j = 0; j < minDegree; j++) {
                    newChild.children[j] = child.children[j + minDegree];
                }
            }
            child.keyCount = minDegree - 1;
            for (int j = keyCount; j > childIndex; j--) {
                children[j + 1] = children[j];
            }
            children[childIndex + 1] = newChild;
            for (int j = keyCount - 1; j >= childIndex; j--) {
                keys[j + 1] = keys[j];
            }
            keys[childIndex] = child.keys[minDegree - 1];
            keyCount++;
        }

        void collectKeys(List<Integer> result) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].collectKeys(result);
                }
                result.add(keys[i]);
            }
            if (!leaf) {
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
            if (leaf) {
                return false;
            }
            return children[i].containsKey(key);
        }

        boolean isFull() {
            return keyCount == 2 * minDegree - 1;
        }
    }
}
