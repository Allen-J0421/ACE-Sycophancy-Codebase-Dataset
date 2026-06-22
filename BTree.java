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
            root = new LeafNode(minDegree);
        }
        if (root.isFull()) {
            root = growRoot(key);
        } else {
            root.insert(key);
        }
    }

    private BTreeNode growRoot(int key) {
        InternalNode newRoot = new InternalNode(minDegree);
        newRoot.initAsNewRoot(root, key);
        return newRoot;
    }

    private static abstract class BTreeNode {
        final int[] keys;
        final int minDegree;
        int keyCount;

        BTreeNode(int minDegree) {
            this.minDegree = minDegree;
            this.keys = new int[2 * minDegree - 1];
            this.keyCount = 0;
        }

        abstract void insert(int key);
        abstract void collectKeys(List<Integer> result);
        abstract boolean containsKey(int key);
        abstract BTreeNode createSibling();

        boolean isFull() {
            return keyCount == 2 * minDegree - 1;
        }
    }

    private static class LeafNode extends BTreeNode {
        LeafNode(int minDegree) {
            super(minDegree);
        }

        @Override
        void insert(int key) {
            int i = keyCount - 1;
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            keyCount++;
        }

        @Override
        void collectKeys(List<Integer> result) {
            for (int i = 0; i < keyCount; i++) {
                result.add(keys[i]);
            }
        }

        @Override
        boolean containsKey(int key) {
            int i = 0;
            while (i < keyCount && key > keys[i]) {
                i++;
            }
            return i < keyCount && key == keys[i];
        }

        @Override
        BTreeNode createSibling() {
            LeafNode sibling = new LeafNode(minDegree);
            sibling.keyCount = minDegree - 1;
            for (int j = 0; j < minDegree - 1; j++) {
                sibling.keys[j] = keys[j + minDegree];
            }
            return sibling;
        }
    }

    private static class InternalNode extends BTreeNode {
        final BTreeNode[] children;

        InternalNode(int minDegree) {
            super(minDegree);
            this.children = new BTreeNode[2 * minDegree];
        }

        @Override
        void insert(int key) {
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

        @Override
        void collectKeys(List<Integer> result) {
            for (int i = 0; i < keyCount; i++) {
                children[i].collectKeys(result);
                result.add(keys[i]);
            }
            children[keyCount].collectKeys(result);
        }

        @Override
        boolean containsKey(int key) {
            int i = 0;
            while (i < keyCount && key > keys[i]) {
                i++;
            }
            if (i < keyCount && key == keys[i]) {
                return true;
            }
            return children[i].containsKey(key);
        }

        @Override
        BTreeNode createSibling() {
            InternalNode sibling = new InternalNode(minDegree);
            sibling.keyCount = minDegree - 1;
            for (int j = 0; j < minDegree - 1; j++) {
                sibling.keys[j] = keys[j + minDegree];
            }
            for (int j = 0; j < minDegree; j++) {
                sibling.children[j] = children[j + minDegree];
            }
            return sibling;
        }

        void splitChild(int childIndex) {
            BTreeNode child = children[childIndex];
            BTreeNode sibling = child.createSibling();
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

        void initAsNewRoot(BTreeNode fullChild, int key) {
            children[0] = fullChild;
            splitChild(0);
            int childIndex = keys[0] < key ? 1 : 0;
            children[childIndex].insert(key);
        }
    }
}
