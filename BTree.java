import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.IntConsumer;

public final class BTree {
    private final int minDegree;
    private Node root;

    public BTree(int minDegree) {
        validateMinDegree(minDegree);
        this.minDegree = minDegree;
    }

    public void insert(int key) {
        if (root == null) {
            root = createLeafNode(key);
            return;
        }

        splitRootIfNeeded();
        root.insertNonFull(key);
    }

    public void insertAll(int... keys) {
        Objects.requireNonNull(keys, "keys");
        for (int key : keys) {
            insert(key);
        }
    }

    public boolean contains(int key) {
        return root != null && root.contains(key);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void forEachKey(IntConsumer action) {
        Objects.requireNonNull(action, "action");
        if (root != null) {
            root.forEachKey(action);
        }
    }

    public List<Integer> toList() {
        List<Integer> orderedKeys = new ArrayList<>();
        forEachKey(orderedKeys::add);
        return orderedKeys;
    }

    private void splitRootIfNeeded() {
        if (!root.isFull()) {
            return;
        }

        Node newRoot = createInternalNode();
        newRoot.setChild(0, root);
        newRoot.splitChild(0);
        root = newRoot;
    }

    private static void validateMinDegree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("B-tree minimum degree must be at least 2.");
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ");
        forEachKey(key -> joiner.add(Integer.toString(key)));
        return joiner.toString();
    }

    private Node createLeafNode(int firstKey) {
        Node node = new Node(true);
        node.keys[0] = firstKey;
        node.keyCount = 1;
        return node;
    }

    private Node createInternalNode() {
        return new Node(false);
    }

    private int maxKeysPerNode() {
        return 2 * minDegree - 1;
    }

    private int maxChildrenPerNode() {
        return 2 * minDegree;
    }

    private int medianKeyIndex() {
        return minDegree - 1;
    }

    private final class Node {
        private final int[] keys;
        private final Node[] children;
        private final boolean leaf;
        private int keyCount;

        private Node(boolean leaf) {
            this.leaf = leaf;
            this.keys = new int[maxKeysPerNode()];
            this.children = new Node[maxChildrenPerNode()];
        }

        private boolean isFull() {
            return keyCount == keys.length;
        }

        private void setChild(int index, Node child) {
            children[index] = child;
        }

        private boolean contains(int targetKey) {
            int index = findFirstGreaterOrEqualIndex(targetKey);
            if (matchesKeyAt(index, targetKey)) {
                return true;
            }

            return !leaf && children[index].contains(targetKey);
        }

        private void forEachKey(IntConsumer action) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].forEachKey(action);
                }
                action.accept(keys[i]);
            }

            if (!leaf) {
                children[keyCount].forEachKey(action);
            }
        }

        private void insertNonFull(int key) {
            if (leaf) {
                insertIntoLeaf(key);
                return;
            }

            int childIndex = findFirstGreaterOrEqualIndex(key);
            childIndex = prepareChildForInsert(childIndex, key);
            children[childIndex].insertNonFull(key);
        }

        private void insertIntoLeaf(int key) {
            int insertAt = findFirstGreaterOrEqualIndex(key);
            shiftKeysRightFrom(insertAt);
            keys[insertAt] = key;
            keyCount++;
        }

        private int prepareChildForInsert(int childIndex, int key) {
            if (!children[childIndex].isFull()) {
                return childIndex;
            }

            splitChild(childIndex);
            if (key > keys[childIndex]) {
                return childIndex + 1;
            }
            return childIndex;
        }

        private void splitChild(int childIndex) {
            Node child = children[childIndex];
            Node sibling = new Node(child.leaf);
            int medianKey = child.keys[medianKeyIndex()];

            sibling.keyCount = minDegree - 1;
            copyUpperHalfOfKeysToSibling(child, sibling);
            if (!child.leaf) {
                copyUpperHalfOfChildrenToSibling(child, sibling);
            }

            child.keyCount = minDegree - 1;
            shiftChildrenRightFrom(childIndex + 1);
            children[childIndex + 1] = sibling;

            shiftKeysRightFrom(childIndex);
            keys[childIndex] = medianKey;
            keyCount++;
        }

        private void copyUpperHalfOfKeysToSibling(Node source, Node sibling) {
            System.arraycopy(source.keys, minDegree, sibling.keys, 0, minDegree - 1);
        }

        private void copyUpperHalfOfChildrenToSibling(Node source, Node sibling) {
            System.arraycopy(source.children, minDegree, sibling.children, 0, minDegree);
        }

        private void shiftChildrenRightFrom(int fromIndex) {
            int length = keyCount - fromIndex + 1;
            System.arraycopy(children, fromIndex, children, fromIndex + 1, length);
        }

        private void shiftKeysRightFrom(int fromIndex) {
            int length = keyCount - fromIndex;
            System.arraycopy(keys, fromIndex, keys, fromIndex + 1, length);
        }

        private int findFirstGreaterOrEqualIndex(int targetKey) {
            int index = 0;
            while (index < keyCount && targetKey > keys[index]) {
                index++;
            }
            return index;
        }

        private boolean matchesKeyAt(int index, int targetKey) {
            return index < keyCount && keys[index] == targetKey;
        }
    }
}
