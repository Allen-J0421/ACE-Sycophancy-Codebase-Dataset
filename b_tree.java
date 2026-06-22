import java.util.ArrayList;
import java.util.List;

class BTree {
    private final int minDegree;
    private Node root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("B-tree minimum degree must be at least 2.");
        }

        this.minDegree = minDegree;
    }

    void insert(int key) {
        if (root == null) {
            root = Node.leaf(minDegree, key);
            return;
        }

        if (root.isFull()) {
            Node newRoot = Node.internal(minDegree);
            newRoot.setChild(0, root);
            newRoot.splitChild(0);
            root = newRoot;
        }

        root.insertNonFull(key);
    }

    boolean contains(int key) {
        return root != null && root.contains(key);
    }

    String traversal() {
        List<Integer> orderedKeys = new ArrayList<>();
        if (root != null) {
            root.collectKeys(orderedKeys);
        }
        return formatKeys(orderedKeys);
    }

    void traverse() {
        System.out.print(traversal());
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
            this.keys = new int[2 * minDegree - 1];
            this.children = new Node[2 * minDegree];
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

        private boolean isFull() {
            return keyCount == keys.length;
        }

        private void setChild(int index, Node child) {
            children[index] = child;
        }

        private boolean contains(int targetKey) {
            int keyIndex = findSearchIndex(targetKey);
            if (keyIndex < keyCount && keys[keyIndex] == targetKey) {
                return true;
            }

            return !leaf && children[keyIndex].contains(targetKey);
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

            int childIndex = findInsertChildIndex(key);
            if (children[childIndex].isFull()) {
                splitChild(childIndex);
                if (key > keys[childIndex]) {
                    childIndex++;
                }
            }

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

        private void splitChild(int childIndex) {
            Node child = children[childIndex];
            Node sibling = new Node(child.minDegree, child.leaf);
            int medianKey = child.keys[minDegree - 1];

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

        private int findSearchIndex(int targetKey) {
            int index = 0;
            while (index < keyCount && targetKey > keys[index]) {
                index++;
            }
            return index;
        }

        private int findInsertChildIndex(int key) {
            int index = keyCount - 1;
            while (index >= 0 && keys[index] > key) {
                index--;
            }
            return index + 1;
        }
    }
}

class Main {
    public static void main(String[] args) {
        BTree tree = new BTree(3);
        tree.insert(10);
        tree.insert(20);
        tree.insert(5);
        tree.insert(6);
        tree.insert(12);
        tree.insert(30);
        tree.insert(7);
        tree.insert(17);

        System.out.print("Traversal of the constructed tree is");
        tree.traverse();
        System.out.println();

        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
