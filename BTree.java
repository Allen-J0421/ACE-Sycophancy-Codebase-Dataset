import java.util.ArrayList;
import java.util.List;

public final class BTree {
    private final int minDegree;
    private Node root;

    public BTree(int minDegree) {
        validateMinDegree(minDegree);
        this.minDegree = minDegree;
    }

    public void insert(int key) {
        if (root == null) {
            root = Node.leaf(minDegree, key);
            return;
        }

        splitRootIfNeeded();
        root.insertNonFull(key);
    }

    public boolean contains(int key) {
        return root != null && root.contains(key);
    }

    public List<Integer> toList() {
        List<Integer> orderedKeys = new ArrayList<>();
        if (root != null) {
            root.collectKeys(orderedKeys);
        }
        return orderedKeys;
    }

    public String traversal() {
        return formatKeys(toList());
    }

    public void traverse() {
        System.out.print(traversal());
    }

    private void splitRootIfNeeded() {
        if (!root.isFull()) {
            return;
        }

        Node newRoot = Node.internal(minDegree);
        newRoot.setChild(0, root);
        newRoot.splitChild(0);
        root = newRoot;
    }

    private static void validateMinDegree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("B-tree minimum degree must be at least 2.");
        }
    }

    private static String formatKeys(List<Integer> keys) {
        StringBuilder builder = new StringBuilder();
        for (int key : keys) {
            builder.append(' ').append(key);
        }
        return builder.toString();
    }

    private static final class Node {
        private final int minDegree;
        private final int[] keys;
        private final Node[] children;
        private final boolean leaf;
        private int keyCount;

        private Node(int minDegree, boolean leaf) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new int[maxKeysFor(minDegree)];
            this.children = new Node[maxChildrenFor(minDegree)];
        }

        private static Node leaf(int minDegree, int firstKey) {
            Node node = new Node(minDegree, true);
            node.keys[0] = firstKey;
            node.keyCount = 1;
            return node;
        }

        private static Node internal(int minDegree) {
            return new Node(minDegree, false);
        }

        private static int maxKeysFor(int minDegree) {
            return 2 * minDegree - 1;
        }

        private static int maxChildrenFor(int minDegree) {
            return 2 * minDegree;
        }

        private boolean isFull() {
            return keyCount == keys.length;
        }

        private void setChild(int index, Node child) {
            children[index] = child;
        }

        private boolean contains(int targetKey) {
            int index = findKeyIndex(targetKey);
            if (matchesKeyAt(index, targetKey)) {
                return true;
            }

            return !leaf && children[index].contains(targetKey);
        }

        private void collectKeys(List<Integer> destination) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].collectKeys(destination);
                }
                destination.add(keys[i]);
            }

            if (!leaf) {
                children[keyCount].collectKeys(destination);
            }
        }

        private void insertNonFull(int key) {
            if (leaf) {
                insertIntoLeaf(key);
                return;
            }

            int childIndex = findKeyIndex(key);
            childIndex = prepareChildForInsert(childIndex, key);
            children[childIndex].insertNonFull(key);
        }

        private void insertIntoLeaf(int key) {
            int insertAt = keyCount - 1;
            while (insertAt >= 0 && keys[insertAt] > key) {
                keys[insertAt + 1] = keys[insertAt];
                insertAt--;
            }

            keys[insertAt + 1] = key;
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
            Node sibling = new Node(child.minDegree, child.leaf);
            int medianKey = child.keys[medianKeyIndex()];

            sibling.keyCount = minDegree - 1;
            copyUpperHalfOfKeys(child, sibling);
            if (!child.leaf) {
                copyUpperHalfOfChildren(child, sibling);
            }

            child.keyCount = minDegree - 1;
            shiftChildrenRight(childIndex + 1);
            children[childIndex + 1] = sibling;

            shiftKeysRight(childIndex);
            keys[childIndex] = medianKey;
            keyCount++;
        }

        private void copyUpperHalfOfKeys(Node source, Node destination) {
            for (int i = 0; i < minDegree - 1; i++) {
                destination.keys[i] = source.keys[i + minDegree];
            }
        }

        private void copyUpperHalfOfChildren(Node source, Node destination) {
            for (int i = 0; i < minDegree; i++) {
                destination.children[i] = source.children[i + minDegree];
            }
        }

        private void shiftChildrenRight(int fromIndex) {
            for (int i = keyCount; i >= fromIndex; i--) {
                children[i + 1] = children[i];
            }
        }

        private void shiftKeysRight(int fromIndex) {
            for (int i = keyCount - 1; i >= fromIndex; i--) {
                keys[i + 1] = keys[i];
            }
        }

        private int findKeyIndex(int targetKey) {
            int index = 0;
            while (index < keyCount && targetKey > keys[index]) {
                index++;
            }
            return index;
        }

        private boolean matchesKeyAt(int index, int targetKey) {
            return index < keyCount && keys[index] == targetKey;
        }

        private int medianKeyIndex() {
            return minDegree - 1;
        }
    }
}
