import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

final class BTree {
    private static final class Node {
        private final int minDegree;
        private final int[] keys;
        private final Node[] children;
        private int keyCount;
        private final boolean leaf;

        private Node(int minDegree, boolean leaf) {
            if (minDegree < 2) {
                throw new IllegalArgumentException("minDegree must be at least 2");
            }
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new int[2 * minDegree - 1];
            this.children = new Node[2 * minDegree];
        }

        private boolean isFull() {
            return keyCount == keys.length;
        }

        private int findSearchIndex(int key) {
            int index = 0;
            while (index < keyCount && keys[index] < key) {
                index++;
            }
            return index;
        }

        private int findInsertionIndex(int key) {
            int index = 0;
            while (index < keyCount && keys[index] <= key) {
                index++;
            }
            return index;
        }

        private void insertKeyAt(int index, int key) {
            for (int i = keyCount - 1; i >= index; i--) {
                keys[i + 1] = keys[i];
            }
            keys[index] = key;
            keyCount++;
        }

        private void insertChildAt(int index, Node child) {
            for (int i = keyCount; i > index; i--) {
                children[i + 1] = children[i];
            }
            children[index + 1] = child;
        }

        private void insertNonFull(int key) {
            if (leaf) {
                insertKeyAt(findInsertionIndex(key), key);
                return;
            }

            int childIndex = findInsertionIndex(key);
            if (children[childIndex].isFull()) {
                splitChild(childIndex);
                if (keys[childIndex] < key) {
                    childIndex++;
                }
            }
            children[childIndex].insertNonFull(key);
        }

        private void splitChild(int childIndex) {
            Node fullChild = children[childIndex];
            Node sibling = new Node(minDegree, fullChild.leaf);
            sibling.keyCount = minDegree - 1;

            for (int i = 0; i < minDegree - 1; i++) {
                sibling.keys[i] = fullChild.keys[i + minDegree];
            }

            if (!fullChild.leaf) {
                for (int i = 0; i < minDegree; i++) {
                    sibling.children[i] = fullChild.children[i + minDegree];
                }
            }

            fullChild.keyCount = minDegree - 1;

            children[childIndex] = fullChild;
            insertChildAt(childIndex, sibling);
            insertKeyAt(childIndex, fullChild.keys[minDegree - 1]);
        }

        private boolean contains(int key) {
            int index = findSearchIndex(key);

            if (index < keyCount && keys[index] == key) {
                return true;
            }
            if (leaf) {
                return false;
            }
            return children[index].contains(key);
        }

        private List<Integer> keysInOrder() {
            List<Integer> output = new ArrayList<>();
            forEachKeyInOrder(output::add);
            return output;
        }

        private void forEachKeyInOrder(IntConsumer consumer) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].forEachKeyInOrder(consumer);
                }
                consumer.accept(keys[i]);
            }
            if (!leaf) {
                children[keyCount].forEachKeyInOrder(consumer);
            }
        }
    }

    private final int minDegree;
    private Node root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
    }

    List<Integer> keysInOrder() {
        return root == null ? new ArrayList<>() : root.keysInOrder();
    }

    boolean contains(int key) {
        return root != null && root.contains(key);
    }

    void insert(int key) {
        if (root == null) {
            root = new Node(minDegree, true);
            root.insertNonFull(key);
            return;
        }

        if (root.isFull()) {
            Node newRoot = new Node(minDegree, false);
            newRoot.children[0] = root;
            newRoot.splitChild(0);
            newRoot.insertNonFull(key);
            root = newRoot;
            return;
        }

        root.insertNonFull(key);
    }
}
